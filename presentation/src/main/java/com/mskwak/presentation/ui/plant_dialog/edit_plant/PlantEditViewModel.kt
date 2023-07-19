package com.mskwak.presentation.ui.plant_dialog.edit_plant

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mskwak.domain.usecase.PictureUseCase
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.presentation.R
import com.mskwak.presentation.model.AlarmUiData
import com.mskwak.presentation.model.PlantUiData
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class PlantEditViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val pictureUseCase: PictureUseCase
) : ViewModel() {

    val plantName = MutableLiveData<String>()
    val memo = MutableLiveData<String>()

    private val _pictureUri = MutableLiveData<Uri>()
    private val _plantNameEmpty = MutableLiveData(false)
    private val _plantDate = MutableLiveData(LocalDate.now())
    private val _lastWateringDate = MutableLiveData(LocalDate.now())
    private val _wateringPeriod = MutableLiveData(1)
    private val _wateringAlarmOnOff = MutableLiveData(false)
    private val _wateringAlarmTime = MutableLiveData(LocalTime.of(9, 0))

    private val _onSavedEvent = SingleLiveEvent<Unit>()
    private val _snackbarMessage = SingleLiveEvent<Int>()

    val pictureUri: LiveData<Uri> = _pictureUri
    val plantNameEmpty: LiveData<Boolean> = _plantNameEmpty
    val plantDate: LiveData<LocalDate> = _plantDate
    val lastWateringDate: LiveData<LocalDate> = _lastWateringDate
    val wateringPeriod: LiveData<Int> = _wateringPeriod
    val wateringAlarmOnOff: LiveData<Boolean> = _wateringAlarmOnOff
    val wateringAlarmTime: LiveData<LocalTime> = _wateringAlarmTime

    val onSavedEvent: LiveData<Unit> = _onSavedEvent
    val snackbarMessage: LiveData<Int> = _snackbarMessage

    private var isUpdatePlant = false
    private var plantId = 0
    private var newPictureBitmap: Bitmap? = null
    fun onWateringAlarmOnOffClick() {
        _wateringAlarmOnOff.value = !wateringAlarmOnOff.value!!
    }

    fun onSaveClick() {
        if (plantName.value.isNullOrBlank()) {
            _plantNameEmpty.value = true
            _snackbarMessage.value = R.string.message_input_required_field
            return
        }

        viewModelScope.launch {
            //사진 저장 후 uri받아오기
            val pictureUri = newPictureBitmap?.let {
                pictureUseCase.savePicture(it)
            } ?: _pictureUri.value

            //물주기 기간이 없음일경우 alarm onOff = false
            if (_wateringPeriod.value == 0) {
                _wateringAlarmOnOff.value = false
            }

            val alarm = AlarmUiData(_wateringAlarmTime.value!!, wateringAlarmOnOff.value!!)
            val plant = PlantUiData(
                plantName.value!!,
                _plantDate.value!!,
                _wateringPeriod.value!!,
                _lastWateringDate.value!!,
                alarm,
                pictureUri,
                memo.value,
                plantId
            )
            if (isUpdatePlant) {
                //delete old picture
                if (newPictureBitmap != null) {
                    this@PlantEditViewModel.pictureUri.value?.let { pictureUseCase.deletePicture(it) }
                }
                plantUseCase.updatePlant(plant)
            } else {
                plantUseCase.addPlant(plant)
            }
            _onSavedEvent.call()
        }
    }

    fun loadPlant(plantId: Int) {
        viewModelScope.launch {
            plantUseCase.getPlant(plantId).let { plant ->
                this@PlantEditViewModel.plantId = plant.id
                _pictureUri.value = plant.pictureUri
                plantName.value = plant.name
                _plantDate.value = plant.createdDate
                memo.value = plant.memo
                _lastWateringDate.value = plant.lastWateringDate
                _wateringPeriod.value = plant.waterPeriod
                _wateringAlarmOnOff.value = plant.wateringAlarm.onOff
                _wateringAlarmTime.value = plant.wateringAlarm.time
                isUpdatePlant = true
            }
        }
    }

    fun setNewPicture(bitmap: Bitmap) {
        newPictureBitmap = bitmap
    }

    fun setPlantDate(date: LocalDate) {
        _plantDate.value = date
    }

    fun setLastWateringDate(date: LocalDate) {
        _lastWateringDate.value = date
    }

    fun setWateringPeriod(days: Int) {
        _wateringPeriod.value = days
    }

    fun setWateringAlarmTime(time: LocalTime) {
        _wateringAlarmTime.value = time
    }
}