package com.mskwak.presentation.plant_dialog.plant_detail

import androidx.lifecycle.*
import com.mskwak.domain.usecase.GardenUseCase
import com.mskwak.presentation.model.DiaryImpl
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class PlantDetailViewModel @AssistedInject constructor(
    @Assisted private val plantId: Int,
    private val useCase: GardenUseCase
) : ViewModel() {

    var plant = useCase.getPlantLiveData(plantId)
    var diaries = useCase.getDiariesByPlantId(plantId).map { list ->
        list.map { DiaryImpl(it) }
    }
    val isEmptyList: LiveData<Boolean> = diaries.map {
        it.isNullOrEmpty()
    }
    private val _wateringCompleted = SingleLiveEvent<Unit>()
    val wateringCompleted: LiveData<Unit> = _wateringCompleted

    fun wateringAlarmToggle() {
        plant.value?.wateringAlarm?.onOff?.let {
            val isActive = !it
            useCase.updateWateringAlarmOnOff(isActive, plantId)
        }
    }

    fun watering() {
        viewModelScope.launch {
            useCase.wateringNow(plantId)
            _wateringCompleted.call()
        }
    }

    fun getDdays(): String {
        return plant.value?.let { useCase.getDdayText(it) } ?: ""
    }

    fun deletePlant() {
        if (plant.value != null) useCase.deletePlant(plant.value!!)
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