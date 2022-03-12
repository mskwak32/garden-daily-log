package com.mskwak.presentation.diary.diary_detail

import androidx.lifecycle.*
import com.mskwak.domain.usecase.GardenUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DiaryDetailViewModel @AssistedInject constructor(
    @Assisted private val diaryId: Int,
    private val useCase: GardenUseCase
) : ViewModel() {
    val diary = useCase.observeDiaryById(diaryId)

    val plantName: LiveData<String> = diary.switchMap {
        liveData {
            useCase.getPlantName(it.plantId)
        }
    }

    val isPictureListEmpty = diary.map {
        it.pictureList.isNullOrEmpty()
    }


    fun deleteDiary() {
        diary.value?.let { useCase.deleteDiary(it) }
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