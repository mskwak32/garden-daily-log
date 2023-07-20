package com.mskwak.presentation.ui.dialog_diary.diary_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.mskwak.domain.model.Diary
import com.mskwak.domain.usecase.DiaryUseCase
import com.mskwak.domain.usecase.PlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val diaryUseCase: DiaryUseCase
) : ViewModel() {

    private val _diary = MutableLiveData<Diary>()

    val diary: LiveData<Diary> = _diary
    val plantName: LiveData<String> = _diary.switchMap {
        liveData(context = viewModelScope.coroutineContext) {
            emit(plantUseCase.getPlantName(it.plantId))
        }
    }
    val isPictureListEmpty = diary.map {
        it.pictureList.isNullOrEmpty()
    }

    private var diaryId: Int? = null

    fun loadDiary(diaryId: Int) {
        this.diaryId = diaryId
        viewModelScope.launch {
            diaryUseCase.getDiaryFlow(diaryId).collectLatest {
                _diary.value = it
            }
        }
    }

    fun deleteDiary() {
        viewModelScope.launch {
            diary.value?.let { diaryUseCase.deleteDiary(it) }
        }
    }
}