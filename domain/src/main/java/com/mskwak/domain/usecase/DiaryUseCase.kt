package com.mskwak.domain.usecase

import com.mskwak.domain.AppConstValue
import com.mskwak.domain.model.Diary
import com.mskwak.domain.repository.DiaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryUseCase(
    private val diaryRepository: DiaryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun getDiariesByPlantId(plantId: Int): Flow<List<Diary>> {
        return diaryRepository.getDiariesByPlantId(
            plantId,
            AppConstValue.MAX_DIARY_SIZE_ON_PLANT_DETAIL
        )
    }

    suspend fun addDiary(diary: Diary) {
        withContext(ioDispatcher) {
            diaryRepository.addDiary(diary)
        }
    }

    suspend fun updateDiary(diary: Diary) {
        withContext(ioDispatcher) {
            diaryRepository.updateDiary(diary)
        }
    }

    fun deleteDiary(diary: Diary) {
        CoroutineScope(ioDispatcher).launch {
            diaryRepository.deleteDiary(diary)
        }
    }

    suspend fun getDiary(diaryId: Int): Diary {
        return diaryRepository.getDiary(diaryId)
    }

    fun getDiaryFlow(diaryId: Int): Flow<Diary> {
        return diaryRepository.getDiaryFlow(diaryId)
    }

    fun getDiaries(
        year: Int,
        month: Int,
        sortOrder: DiaryListSortOrder,
        plantId: Int? = null
    ): Flow<List<Diary>> {
        return diaryRepository.getDiaries(year, month, plantId).map { list ->
            list.applySort(sortOrder)
        }.flowOn(ioDispatcher)
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