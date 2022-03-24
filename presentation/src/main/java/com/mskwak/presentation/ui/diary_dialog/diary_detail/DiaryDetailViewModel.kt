package com.mskwak.presentation.ui.diary_dialog.diary_detail

import androidx.lifecycle.*
import com.mskwak.domain.usecase.DiaryUseCase
import com.mskwak.domain.usecase.PlantUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DiaryDetailViewModel @AssistedInject constructor(
    @Assisted private val diaryId: Int,
    private val plantUseCase: PlantUseCase,
    private val diaryUseCase: DiaryUseCase
) : ViewModel() {
    val diary = diaryUseCase.getDiaryLiveData(diaryId)

    val plantName: LiveData<String> = diary.switchMap {
        liveData(context = viewModelScope.coroutineContext) {
            emit(plantUseCase.getPlantName(it.plantId))
        }
    }

    val isPictureListEmpty = diary.map {
        it.pictureList.isNullOrEmpty()
    }


    fun deleteDiary() {
        diary.value?.let { diaryUseCase.deleteDiary(it) }
    }

    @AssistedFactory
    interface DiaryDetailViewModelFactory {
        fun create(diaryId: Int): DiaryDetailViewModel
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: DiaryDetailViewModelFactory,
            diaryId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(diaryId) as T
            }
        }
    }
}