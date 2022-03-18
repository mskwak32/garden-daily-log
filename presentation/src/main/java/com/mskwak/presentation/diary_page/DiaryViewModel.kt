package com.mskwak.presentation.diary_page

import androidx.lifecycle.*
import com.mskwak.domain.model.Diary
import com.mskwak.domain.usecase.DiaryListSortOrder
import com.mskwak.domain.usecase.GardenUseCase
import com.mskwak.presentation.model.DiaryImpl
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
    private val useCase: GardenUseCase
) : ViewModel() {
    private val _month = MutableLiveData(LocalDate.now().withDayOfMonth(1))
    val month: LiveData<LocalDate> = _month

    private val _plantNameMap = SingleLiveEvent<Map<Int, String>>()
    val plantNameMap: LiveData<Map<Int, String>> = _plantNameMap

    private var diarySource: LiveData<List<DiaryImpl>>? = null
    private val _diaries = SingleMediatorLiveData<List<Diary>>()
    val diaries: LiveData<List<Diary>> = _diaries

    var selectedPlantId = SELECT_ALL_KEY
    private var sortOrder = DiaryListSortOrder.CREATED_LATEST
    private var loadJob: Job? = null

    val isEmptyList: LiveData<Boolean> = diaries.map { it.isNullOrEmpty() }


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
        _month.value = LocalDate.of(year, month, 1)
        loadDiaries()
    }

    fun loadPlantNames() = viewModelScope.launch {
        _plantNameMap.value = useCase.getPlantNames()
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

            val newSource = useCase.getDiaries(
                month.value!!.year,
                month.value!!.monthValue,
                sortOrder,
                if (selectedPlantId != SELECT_ALL_KEY) selectedPlantId else null
            ).map {
                it.map { diary -> DiaryImpl(diary) }
            }

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