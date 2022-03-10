package com.mskwak.presentation.diary.diary_detail

import android.net.Uri
import androidx.lifecycle.*
import com.mskwak.domain.usecase.GardenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val useCase: GardenUseCase
) : ViewModel() {

    private val _plantName = MutableLiveData<String>()
    val plantName: LiveData<String> = _plantName

    private val _date = MutableLiveData<LocalDate>()
    val date: LiveData<LocalDate> = _date

    private val _memo = MutableLiveData<String>()
    val memo: LiveData<String> = _memo

    private val _pictureList = MutableLiveData<List<Uri>>()
    val pictureList: LiveData<List<Uri>> = _pictureList

    val isPictureListEmpty = pictureList.switchMap {
        liveData {
            emit(it.isNullOrEmpty())
        }
    }

    fun loadDiary(recordId: Int) {
        viewModelScope.launch {
            useCase.getRecordById(recordId).let {
                _plantName.value = useCase.getPlantName(it.plantId)
                _date.value = it.createdDate
                _memo.value = it.memo
                _pictureList.value = it.pictureList
            }
        }
    }
}