package com.mskwak.presentation.ui.home

import androidx.lifecycle.*
import com.mskwak.domain.model.Plant
import com.mskwak.domain.usecase.PlantListSortOrder
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.presentation.model.PlantImpl
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: PlantUseCase
) : ViewModel() {

    private val _isEmptyList = MutableLiveData(false)
    val isEmptyList: LiveData<Boolean> = _isEmptyList
    private val _sortOrder = MutableLiveData<PlantListSortOrder>()

    private val _openPlantEvent = SingleLiveEvent<Int>()
    val openPlantEvent: LiveData<Int> = _openPlantEvent
    private val _deletePlantClickEvent = SingleLiveEvent<Plant>()
    val deletePlantClickEvent: LiveData<Plant> = _deletePlantClickEvent

    var plants: LiveData<List<Plant>> = _sortOrder.switchMap { sortOrder ->
        useCase.getPlantsWithSortOrder(sortOrder).map {
            _isEmptyList.value = it.isNullOrEmpty()
            it.map { plant -> PlantImpl(plant) }
        }
    }

    fun setSortOrder(sortOrder: PlantListSortOrder) {
        //중복으로 plant list 정렬하여 불러오는 것 방지
        //plant를 새로 추가한 후 fragment view가 재생성되면서 sortOrder를 다시 set하는 것을 방지
        //같은 정렬상태이면 LiveData를 다시 불러올 필요없음
        _sortOrder.takeIf { it.value != sortOrder }?.value = sortOrder
    }

    fun openPlant(plant: Plant) {
        _openPlantEvent.value = plant.id
    }

    fun onDeletePlantClick(plant: Plant) {
        _deletePlantClickEvent.value = plant
    }

    fun deletePlant(plant: Plant) {
        useCase.deletePlant(plant)
    }

    /**
     * return Pair(d-day, isDateOver)
     */
    fun getDdays(plant: Plant): Pair<String, Boolean> {
        return useCase.getDdayText(plant)
    }
}