package com.mskwak.presentation.ui.dialog_plant.plant_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mskwak.domain.model.Diary
import com.mskwak.domain.model.Plant
import com.mskwak.domain.model.WateringDays
import com.mskwak.domain.usecase.DiaryUseCase
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.domain.usecase.WateringUseCase
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val diaryUseCase: DiaryUseCase,
    private val wateringUseCase: WateringUseCase
) : ViewModel() {

    private val _plant = MutableLiveData<Plant>()
    private val _diaries = MutableLiveData<List<Diary>>()
    private val _wateringCompleted = SingleLiveEvent<Unit>()

    var plant: LiveData<Plant> = _plant
    var diaries: LiveData<List<Diary>> = _diaries
    val isEmptyList: LiveData<Boolean> = _diaries.map { diaries ->
        diaries.isEmpty()
    }
    val wateringCompleted: LiveData<Unit> = _wateringCompleted

    private var plantId: Int? = null

    fun loadData(plantId: Int) {
        this.plantId = plantId
        with(viewModelScope) {
            launch {
                plantUseCase.getPlantFlow(plantId).collectLatest {
                    _plant.value = it
                }
            }
            launch {
                diaryUseCase.getDiariesByPlantId(plantId).collectLatest {
                    _diaries.value = it
                }
            }
        }
    }

    fun wateringAlarmToggle() {
        plant.value?.wateringAlarm?.let { alarm ->
            viewModelScope.launch {
                val isActive = !alarm.onOff
                wateringUseCase.updateWateringAlarmOnOff(plant.value!!.id, isActive)
            }
        }
    }

    fun watering() {
        plant.value?.let {
            viewModelScope.launch {
                wateringUseCase.wateringNow(it)
                delay(200)
                _wateringCompleted.call()
            }
        }
    }

    /**
     * return Pair(d-day, isDateOver)
     */
    fun getWateringDays(): WateringDays? {
        return plant.value?.let { wateringUseCase.getWateringDays(it) }
    }

    fun deletePlant() {
        viewModelScope.launch {
            plant.value?.let { plantUseCase.deletePlant(it) }
        }
    }
}