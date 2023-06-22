package com.mskwak.domain.usecase

import com.mskwak.domain.AppConstValue
import com.mskwak.domain.di.IoDispatcher
import com.mskwak.domain.model.Diary
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.type.DiaryListSortOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    fun getDiariesByPlantId(plantId: Int): Flow<List<Diary>> {
        return diaryRepository.getDiariesByPlantId(
            plantId,
            AppConstValue.MAX_DIARY_SIZE_ON_PLANT_DETAIL
        )
    }

    suspend fun addDiary(diary: Diary) {
        diaryRepository.addDiary(diary)
    }

    suspend fun updateDiary(diary: Diary) {
        diaryRepository.updateDiary(diary)
    }

    suspend fun deleteDiary(diary: Diary) {
        diaryRepository.deleteDiary(diary)
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