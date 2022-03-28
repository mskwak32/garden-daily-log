package com.mskwak.presentation.ui.plant_dialog.plant_detail

import androidx.lifecycle.*
import com.mskwak.domain.usecase.DiaryUseCase
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.presentation.model.DiaryImpl
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlantDetailViewModel @AssistedInject constructor(
    @Assisted private val plantId: Int,
    private val plantUseCase: PlantUseCase,
    diaryUseCase: DiaryUseCase
) : ViewModel() {

    var plant = plantUseCase.getPlantLiveData(plantId)
    var diaries = diaryUseCase.getDiariesByPlantId(plantId).map { list ->
        list.map { DiaryImpl(it) }
    }
    val isEmptyList: LiveData<Boolean> = diaries.map {
        it.isNullOrEmpty()
    }
    private val _wateringCompleted = SingleLiveEvent<Unit>()
    val wateringCompleted: LiveData<Unit> = _wateringCompleted

    fun wateringAlarmToggle() {
        plant.value?.wateringAlarm?.let { alarm ->
            val isActive = !alarm.onOff
            plantUseCase.updateWateringAlarmOnOff(isActive, plant.value!!)
        }
    }

    fun watering() {
        plant.value?.let {
            viewModelScope.launch {
                plantUseCase.wateringNow(it)
                delay(200)
                _wateringCompleted.call()
            }
        }
    }

    /**
     * return Pair(d-day, isDateOver)
     */
    fun getDdays(): Pair<String, Boolean> {
        return plant.value?.let { plantUseCase.getDdayText(it) } ?: Pair("", false)
    }

    fun deletePlant() {
        if (plant.value != null) plantUseCase.deletePlant(plant.value!!)
    }

    @AssistedFactory
    interface PlantDetailViewModelAssistedFactory {
        fun create(plantId: Int): PlantDetailViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: PlantDetailViewModelAssistedFactory,
            plantId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(plantId) as T
            }
        }
    }
}