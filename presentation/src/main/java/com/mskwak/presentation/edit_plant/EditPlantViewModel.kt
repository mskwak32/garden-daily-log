package com.mskwak.presentation.edit_plant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mskwak.domain.usecase.GardenUseCase
import com.mskwak.presentation.R
import com.mskwak.presentation.model.AlarmImpl
import com.mskwak.presentation.model.PlantImpl
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel @Inject constructor(
    private val useCase: GardenUseCase
) : ViewModel() {

    val photoClickEvent = SingleLiveEvent<Unit>()
    val plantDateClickEvent = SingleLiveEvent<Unit>()
    val lastWateringClickEvent = SingleLiveEvent<Unit>()
    val wateringPeriodClickEvent = SingleLiveEvent<Unit>()
    val wateringAlarmClickEvent = SingleLiveEvent<Unit>()
    val onSavedEvent = SingleLiveEvent<Unit>()

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

    fun onPhotoClick() {
        photoClickEvent.call()
    }

    fun onPlantDateClick() {
        plantDateClickEvent.call()
    }

    fun onLastWateringDateClick() {
        lastWateringClickEvent.call()
    }

    fun onWateringPeriodClick() {
        wateringPeriodClickEvent.call()
    }

    fun onWateringAlarmClick() {
        wateringAlarmClickEvent.call()
    }

    fun onWateringAlarmOnOffClick() {
        _wateringAlarmOnOff.value = !wateringAlarmOnOff.value!!
    }

    fun onSaveClick() {
        if (plantName.value.isNullOrBlank()) {
            _plantNameEmpty.value = true
            _snackbarMessage.value = R.string.message_input_required_field
            return
        }

        val alarm = AlarmImpl(wateringAlarmTime.value!!, wateringAlarmOnOff.value!!)
        val plant = PlantImpl(
            plantName.value!!,
            plantDate.value!!,
            wateringPeriod.value!!,
            lastWateringDate.value!!,
            alarm,
            null,
            memo.value
        )
        if (isUpdatePlant) {
            plant.id = plantId
            useCase.updatePlant(plant, viewModelScope) {
                onSavedEvent.call()
            }
        } else {
            useCase.addPlant(plant, viewModelScope) {
                onSavedEvent.call()
            }
        }
    }

    fun loadPlant(plantId: Int) {
        useCase.getPlant(plantId, viewModelScope) { plant ->
            this.plantId = plant.id
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