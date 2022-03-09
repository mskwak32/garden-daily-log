package com.mskwak.presentation.plant.plant_detail

import androidx.lifecycle.*
import com.mskwak.domain.AppSettings
import com.mskwak.domain.usecase.GardenUseCase
import com.mskwak.presentation.model.RecordImpl
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import kotlin.math.min

class PlantDetailViewModel @AssistedInject constructor(
    @Assisted private val plantId: Int,
    private val useCase: GardenUseCase
) : ViewModel() {

    var plant = useCase.observePlant(plantId)
    var records = useCase.observeRecordByPlantId(plantId).map { list ->
        val maxSize = min(list.size, AppSettings.MAX_DIARY_SIZE_ON_PLANT_DETAIL)    //정한 갯수까지만 노출
        list.subList(0, maxSize).map { RecordImpl(it) }
    }
    val isEmptyRecords: LiveData<Boolean> = records.map {
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