package com.mskwak.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.mskwak.domain.model.Plant
import com.mskwak.domain.usecase.GardenUseCase
import com.mskwak.presentation.model.PlantImpl
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: GardenUseCase
) : ViewModel() {

    val isEmptyList = MutableLiveData(false)

    private val _openPlantEvent = SingleLiveEvent<Int>()
    val openPlantEvent: LiveData<Int> = _openPlantEvent

    var plants: LiveData<List<Plant>> = useCase.getPlants().map {
        it.map { plant -> PlantImpl(plant) }
    }

    fun openPlant(plant: Plant) {
        _openPlantEvent.value = plant.id
    }

    fun deletePlant(plant: Plant) {
        useCase.deletePlant(plant)
    }

    fun getRemainWateringDate(plant: Plant): Int {
        return useCase.getRemainWateringDate(plant)
    }
}