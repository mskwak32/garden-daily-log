package com.mskwak.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.mskwak.domain.usecase.PlantListSortOrder
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.presentation.model.PlantUiData
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: PlantUseCase
) : ViewModel() {

    private val _isEmptyList = MutableLiveData(false)
    private val _sortOrder = MutableLiveData<PlantListSortOrder>()
    private val _onWateringEvent = SingleLiveEvent<Unit>()

    val isEmptyList: LiveData<Boolean> = _isEmptyList
    val onWateringEvent: LiveData<Unit> = _onWateringEvent

    var plants: LiveData<List<PlantUiData>> = _sortOrder.switchMap { sortOrder ->
        useCase.getPlantsWithSortOrder(sortOrder).map {
            _isEmptyList.value = it.isEmpty()
            it.map { plant -> PlantUiData(plant) }
        }.asLiveData()
    }

    fun setSortOrder(sortOrder: PlantListSortOrder) {
        _sortOrder.takeIf { it.value != sortOrder }?.value = sortOrder
    }

    fun onWateringClick(plant: PlantUiData) {
        viewModelScope.launch {
            _onWateringEvent.call()
            delay(250)
            useCase.wateringNow(plant)
        }
    }

    /**
     * return Pair(d-day, isDateOver)
     */
    fun getDdays(plant: PlantUiData): Pair<String, Boolean> {
        return useCase.getDdayText(plant)
    }
}