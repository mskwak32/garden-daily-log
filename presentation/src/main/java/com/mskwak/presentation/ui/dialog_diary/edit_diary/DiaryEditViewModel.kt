package com.mskwak.presentation.ui.dialog_diary.edit_diary

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mskwak.domain.AppConstValue
import com.mskwak.domain.model.Diary
import com.mskwak.domain.usecase.DiaryUseCase
import com.mskwak.domain.usecase.PictureUseCase
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.presentation.R
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryEditViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val pictureUseCase: PictureUseCase,
    private val diaryUseCase: DiaryUseCase
) : ViewModel() {

    private val _plantName = MutableLiveData<String>()
    val diaryDate = MutableLiveData(LocalDate.now())
    val contentText = MutableLiveData<String>()
    private val _pictureList = MutableLiveData<MutableList<Uri>>()
    private val _onSavedEvent = SingleLiveEvent<Unit>()
    private val _snackbarMessage = SingleLiveEvent<Int>()

    val plantName: LiveData<String> = _plantName
    val onSavedEvent: LiveData<Unit> = _onSavedEvent
    val snackbarMessage: LiveData<Int> = _snackbarMessage
    val pictureList: LiveData<List<Uri>> = _pictureList.map { it.toList() }

    private var originPictures: List<Uri> = emptyList()     //기존 일지 불러왔을 때 사진목록
    private var newPictures = mutableListOf<Uri>()          //새로 추가된 사진 목록
    private var isUpdate = false
    private var diaryId = 0
    private var plantId: Int? = null

    fun saveDiary() {
        if (_pictureList.value.isNullOrEmpty() && contentText.value.isNullOrBlank()) {
            _snackbarMessage.value = R.string.message_input_picture_or_content
            return
        }

        if (plantId == null) {
            Timber.e("plant id is null")
            throw Exception("must load plant and set plantId before save diary")
        }

        viewModelScope.launch {
            val diary = Diary(
                plantId = plantId!!,
                memo = contentText.value ?: "",
                pictureList = _pictureList.value?.toList() ?: emptyList(),
                createdDate = diaryDate.value!!,
                id = diaryId
            )

            if (isUpdate) {
                deleteOldPicture()
                diaryUseCase.updateDiary(diary)
            } else {
                diaryUseCase.addDiary(diary)
            }
            //새로 추가한 사진 중 diary로 저장되는 사진을 제외하고 세팅 -> 삭제되도록함
            newPictures =
                newPictures.filter { _pictureList.value?.contains(it) == false }.toMutableList()
            _onSavedEvent.call()
        }
    }

    private fun deleteOldPicture() {
        //기존에 있던 사진 중 삭제할 사진골라 삭제
        viewModelScope.launch {
            val toDeleteList = originPictures.filter { _pictureList.value?.contains(it) == false }
            pictureUseCase.deletePicture(*toDeleteList.toTypedArray())
        }
    }

    fun deleteTempFiles() {
        //임시로 추가했던 사진 삭제
        viewModelScope.launch {
            if (newPictures.isNotEmpty()) {
                pictureUseCase.deletePicture(*newPictures.toTypedArray())
            }
        }
    }

    fun isPictureFull(): Boolean {
        return if ((_pictureList.value?.size ?: 0) <= AppConstValue.MAX_PICTURE_PER_DIARY) {
            false
        } else {
            _snackbarMessage.value = R.string.message_no_more_picture
            true
        }
    }

    //새로 추가된 사진을 파일로 저장, 임시 리스트에도 저장
    fun saveNewPicture(bitmap: Bitmap) {
        viewModelScope.launch {
            val uri = pictureUseCase.savePicture(bitmap)
            _pictureList.value = _pictureList.value?.apply { add(uri) } ?: mutableListOf(uri)
            newPictures.add(uri)
        }
    }

    fun deletePicture(uri: Uri) {
        _pictureList.value?.remove(uri)
        _pictureList.value = _pictureList.value
    }

    fun loadDiary(id: Int) {
        viewModelScope.launch {
            diaryUseCase.getDiary(id).first().let {
                diaryId = it.id
                plantId = it.plantId
                diaryDate.value = it.createdDate
                contentText.value = it.memo
                it.pictureList?.let { list ->
                    _pictureList.value = list.toMutableList()
                    originPictures = list
                }
                isUpdate = true
            }
        }
    }

    fun loadPlantName(plantId: Int) {
        viewModelScope.launch {
            this@DiaryEditViewModel.plantId = plantId
            _plantName.value = plantUseCase.getPlantName(plantId)
        }
    }

}