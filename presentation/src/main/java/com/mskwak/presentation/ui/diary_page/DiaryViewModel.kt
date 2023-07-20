package com.mskwak.presentation.ui.diary_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mskwak.domain.model.Diary
import com.mskwak.domain.type.DiaryListSortOrder
import com.mskwak.domain.usecase.DiaryUseCase
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.presentation.util.SingleLiveEvent
import com.mskwak.presentation.util.SingleMediatorLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val diaryUseCase: DiaryUseCase
) : ViewModel() {

    private val _month = MutableLiveData(LocalDate.now().withDayOfMonth(1))
    private val _plantNameMap = SingleLiveEvent<Map<Int, String>>()
    private val _diaries = SingleMediatorLiveData<List<Diary>>()

    val month: LiveData<LocalDate> = _month
    val plantNameMap: LiveData<Map<Int, String>> = _plantNameMap
    val diaries: LiveData<List<Diary>> = _diaries

    private var diarySource: LiveData<List<Diary>>? = null
    val isEmptyList: LiveData<Boolean> = diaries.map { it.isEmpty() }

    var selectedPlantId = SELECT_ALL_KEY
    private var sortOrder = DiaryListSortOrder.CREATED_LATEST
    private var loadJob: Job? = null

    fun previousMonth() {
        _month.value = month.value?.minusMonths(1)
        Timber.d("previous month click")
        loadDiaries()
    }

    fun nextMonth() {
        _month.value = month.value?.plusMonths(1)
        Timber.d("next month click")
        loadDiaries()
    }

    fun setMonth(year: Int, month: Int) {
        if (_month.value?.year == year && _month.value?.monthValue == month) return

        _month.value = LocalDate.of(year, month, 1)
        loadDiaries()
    }

    fun loadPlantNames() = viewModelScope.launch {
        _plantNameMap.value = plantUseCase.getPlantNames()
    }

    fun setPlantFilter(plantId: Int) {
        selectedPlantId = plantId
        Timber.d("set plant filter")
        loadDiaries()
    }

    fun setSortOrder(sortOrder: DiaryListSortOrder) {
        this.sortOrder = sortOrder
        Timber.d("set sort order")
        loadDiaries()
    }

    private fun loadDiaries() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            //식물 이름이 세팅이 안되어있으면 식물이름부터 불러오기
            if (_plantNameMap.value == null) {
                loadPlantNames().join()
            }

            val newSource = diaryUseCase.getDiaries(
                month.value!!.year,
                month.value!!.monthValue,
                sortOrder,
                if (selectedPlantId != SELECT_ALL_KEY) selectedPlantId else null
            ).asLiveData()

            yield()

            if (diarySource != null) {
                _diaries.removeSource(diarySource!!)
                Timber.d("remove diary source")
            }

            diarySource = newSource

            _diaries.addSource(newSource) {
                _diaries.setValue(it)
                Timber.d("add diary source")
            }
        }
    }

    companion object {
        const val SELECT_ALL_KEY = -1
    }
}