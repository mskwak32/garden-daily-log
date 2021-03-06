package com.mskwak.presentation.ui.home

import androidx.lifecycle.*
import com.mskwak.domain.usecase.PlantListSortOrder
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.presentation.model.PlantUiData
import com.mskwak.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: PlantUseCase
) : ViewModel() {

    private val _isEmptyList = MutableLiveData(false)
    val isEmptyList: LiveData<Boolean> = _isEmptyList
    private val _sortOrder = MutableLiveData<PlantListSortOrder>()

    private val _onWateringEvent = SingleLiveEvent<Unit>()
    val onWateringEvent: LiveData<Unit> = _onWateringEvent

    var plants: LiveData<List<PlantUiData>> = _sortOrder.switchMap { sortOrder ->
        useCase.getPlantsWithSortOrder(sortOrder).map {
            _isEmptyList.value = it.isNullOrEmpty()
            it.map { plant -> PlantUiData(plant) }
        }
    }

    fun setSortOrder(sortOrder: PlantListSortOrder) {
        //중복으로 plant list 정렬하여 불러오는 것 방지
        //plant를 새로 추가한 후 fragment view가 재생성되면서 sortOrder를 다시 set하는 것을 방지
        //같은 정렬상태이면 LiveData를 다시 불러올 필요없음
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