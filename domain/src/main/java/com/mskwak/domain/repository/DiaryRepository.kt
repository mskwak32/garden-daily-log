package com.mskwak.domain.repository

import com.mskwak.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {

    suspend fun addDiary(diary: Diary)
    suspend fun updateDiary(diary: Diary)
    suspend fun deleteDiary(diary: Diary)
    fun getDiariesByPlantId(plantId: Int, limit: Int): Flow<List<Diary>>
    fun getDiary(id: Int): Flow<Diary>
    suspend fun deleteDiariesByPlantId(plantId: Int)
    fun getDiaries(year: Int, month: Int, plantId: Int?): Flow<List<Diary>>
}