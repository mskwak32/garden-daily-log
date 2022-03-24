package com.mskwak.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.mskwak.domain.AppConstValue
import com.mskwak.domain.model.Diary
import com.mskwak.domain.repository.DiaryRepository
import kotlinx.coroutines.*

class DiaryUseCase(
    private val diaryRepository: DiaryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun getDiariesByPlantId(plantId: Int): LiveData<List<Diary>> {
        return diaryRepository.getDiariesByPlantId(
            plantId,
            AppConstValue.MAX_DIARY_SIZE_ON_PLANT_DETAIL
        )
    }

    suspend fun addDiary(diary: Diary) = withContext(ioDispatcher) {
        diaryRepository.addDiary(diary)
    }

    suspend fun updateDiary(diary: Diary) = withContext(ioDispatcher) {
        diaryRepository.updateDiary(diary)
    }

    fun deleteDiary(diary: Diary) {
        CoroutineScope(ioDispatcher).launch {
            diaryRepository.deleteDiary(diary)
        }
    }

    suspend fun getDiary(diaryId: Int): Diary {
        return diaryRepository.getDiary(diaryId)
    }

    fun getDiaryLiveData(diaryId: Int): LiveData<Diary> {
        return diaryRepository.getDiaryLiveData(diaryId)
    }

    fun getDiaries(
        year: Int,
        month: Int,
        sortOrder: DiaryListSortOrder,
        plantId: Int? = null
    ): LiveData<List<Diary>> {
        val diariesLiveData = diaryRepository.getDiaries(year, month, plantId)
        return diariesLiveData.switchMap { list ->
            liveData(context = ioDispatcher) {
                emit(list.applySort(sortOrder))
            }
        }
    }

    private fun List<Diary>.applySort(sortOrder: DiaryListSortOrder): List<Diary> {
        return when (sortOrder) {
            DiaryListSortOrder.CREATED_LATEST -> {
                sortedByDescending { diary -> diary.createdDate }
            }
            DiaryListSortOrder.CREATED_OLDEST -> {
                sortedBy { diary -> diary.createdDate }
            }
        }
    }
}