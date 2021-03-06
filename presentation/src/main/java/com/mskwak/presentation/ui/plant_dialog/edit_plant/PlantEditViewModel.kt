package com.mskwak.presentation.ui.plant_dialog.edit_plant

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val useCase: PlantUseCase
) : ViewModel() {

    val onSavedEvent = SingleLiveEvent<Unit>()

    private val _pictureUri = MutableLiveData<Uri>()
    val pictureUri: LiveData<Uri> = _pictureUri

    private var newPictureBitmap: Bitmap? = null
    val plantName = MutableLiveData<String>()

    private val _plantNameEmpty = MutableLiveData(false)
    val plantNameEmpty: LiveData<Boolean> = _plantNameEmpty

    val plantDate = MutableLiveData(LocalDate.now())
    val memo = MutableLiveData<String>()
    val lastWateringDate = MutableLiveData(LocalDate.now())
    val wateringPeriod = MutableLiveData(1)

    private val _wateringAlarmOnOff = MutableLiveData(false)
    val wateringAlarmOnOff: LiveData<Boolean> = _wateringAlarmOnOff

    val wateringAlarmTime = MutableLiveData(LocalTime.of(9, 0))

    private val _snackbarMessage = SingleLiveEvent<Int>()
    val snackbarMessage: LiveData<Int> = _snackbarMessage

    private var isUpdatePlant = false
    private var plantId = 0

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
            //?????? ?????? ??? uri????????????
            val pictureUri = newPictureBitmap?.let {
                useCase.savePicture(it)
            } ?: _pictureUri.value

            //????????? ????????? ??????????????? alarm onOff = false
            if (wateringPeriod.value == 0) {
                _wateringAlarmOnOff.value = false
            }

            val alarm = AlarmUiData(wateringAlarmTime.value!!, wateringAlarmOnOff.value!!)
            val plant = PlantUiData(
                plantName.value!!,
                plantDate.value!!,
                wateringPeriod.value!!,
                lastWateringDate.value!!,
                alarm,
                pictureUri,
                memo.value,
                plantId
            )
            if (isUpdatePlant) {
                //delete old picture
                if (newPictureBitmap != null) {
                    this@PlantEditViewModel.pictureUri.value?.let { useCase.deletePicture(it) }
                }
                useCase.updatePlant(plant)
            } else {
                useCase.addPlant(plant)
            }
            onSavedEvent.call()
        }
    }

    fun loadPlant(plantId: Int) {
        viewModelScope.launch {
            useCase.getPlant(plantId).let { plant ->
                this@PlantEditViewModel.plantId = plant.id
                _pictureUri.value = plant.pictureUri
                plantName.value = plant.name
                plantDate.value = plant.createdDate
                memo.value = plant.memo
                lastWateringDate.value = plant.lastWateringDate
                wateringPeriod.value = plant.waterPeriod
                _wateringAlarmOnOff.value = plant.wateringAlarm.onOff
                wateringAlarmTime.value = plant.wateringAlarm.time
                isUpdatePlant = true
            }
        }
    }

    fun setNewPicture(bitmap: Bitmap) {
        newPictureBitmap = bitmap
    }

}