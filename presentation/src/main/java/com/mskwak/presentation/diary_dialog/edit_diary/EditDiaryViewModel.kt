package com.mskwak.presentation.diary_dialog.edit_diary

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import com.mskwak.domain.AppSettings
import com.mskwak.domain.usecase.GardenUseCase
import com.mskwak.presentation.R
import com.mskwak.presentation.model.DiaryImpl
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditDiaryViewModel @Inject constructor(
    private val useCase: GardenUseCase
) : ViewModel() {
    private var plantId: Int? = null
    private val _plantName = MutableLiveData<String>()
    val plantName: LiveData<String> = _plantName

    val diaryDate = MutableLiveData(LocalDate.now())
    val contentText = MutableLiveData<String>()

    private val _onSavedEvent = SingleLiveEvent<Unit>()
    val onSavedEvent: LiveData<Unit> = _onSavedEvent
    private val _snackbarMessage = SingleLiveEvent<Int>()
    val snackbarMessage: LiveData<Int> = _snackbarMessage

    private var originPictures: List<Uri> = emptyList()     //기존 일지 불러왔을 때 사진목록
    private var newPictures = mutableListOf<Uri>()          //새로 추가된 사진 목록
    private val _pictureList = MutableLiveData<MutableList<Uri>>()
    val pictureList: LiveData<List<Uri>> = _pictureList.map { it.toList() }

    private var isUpdate = false
    private var diaryId = 0


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
            val diary = DiaryImpl(
                plantId!!,
                contentText.value ?: "",
                _pictureList.value?.toList() ?: emptyList(),
                diaryDate.value!!,
                diaryId
            )

            if (isUpdate) {
                deleteOldPicture()
                useCase.updateDiary(diary)
            } else {
                useCase.addDiary(diary)
            }
            //새로 추가한 사진 중 diary로 저장되는 사진을 제외하고 세팅 -> 삭제되도록함
            newPictures =
                newPictures.filter { _pictureList.value?.contains(it) == false }.toMutableList()
            _onSavedEvent.call()
        }
    }

    private fun deleteOldPicture() {
        //기존에 있던 사진 중 삭제할 사진골라 삭제
        val toDeleteList = originPictures.filter { _pictureList.value?.contains(it) == false }
        useCase.deletePicture(*toDeleteList.toTypedArray())
    }

    fun deleteTempFiles() {
        //임시로 추가했던 사진 삭제
        if (newPictures.isNotEmpty()) {
            useCase.deletePicture(*newPictures.toTypedArray())
        }
    }

    fun isPictureFull(): Boolean {
        return if (_pictureList.value?.size ?: 0 <= AppSettings.MAX_PICTURE_PER_DIARY) {
            false
        } else {
            _snackbarMessage.value = R.string.message_no_more_picture
            true
        }
    }

    //새로 추가된 사진을 파일로 저장, 임시 리스트에도 저장
    fun saveNewPicture(bitmap: Bitmap) {
        viewModelScope.launch {
            val uri = useCase.savePicture(bitmap)
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
            useCase.getDiary(id).let {
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
            this@EditDiaryViewModel.plantId = plantId
            _plantName.value = useCase.getPlantName(plantId)
        }
    }

}