package com.mskwak.data.source.local

import androidx.room.*
import com.mskwak.data.model.DiaryData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DiaryDao {

    @Insert
    suspend fun insertDiary(diary: DiaryData)

    @Update
    suspend fun updateDiary(diary: DiaryData)

    @Delete
    suspend fun deleteDiary(diary: DiaryData)

    @Query("SELECT * FROM diary WHERE plantId = :plantId")
    suspend fun getDiariesByPlantId(plantId: Int): List<DiaryData>

    @Query("SELECT * FROM diary WHERE plantId = :plantId ORDER BY createdDate DESC LIMIT :limit")
    fun getDiariesByPlantId(plantId: Int, limit: Int): Flow<List<DiaryData>>

    @Query("SELECT * FROM diary WHERE plantId = :plantId AND (createdDate BETWEEN :startDate AND :endDate)")
    fun getDiariesByPlantId(
        plantId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<DiaryData>>

    @Query("SELECT * FROM diary WHERE id = :id")
    suspend fun getDiary(id: Int): DiaryData

    @Query("SELECT * FROM diary WHERE id = :id")
    fun getDiaryFlow(id: Int): Flow<DiaryData>

    @Query("SELECT * FROM diary WHERE createdDate BETWEEN :startDate AND :endDate")
    fun getDiaries(startDate: LocalDate, endDate: LocalDate): Flow<List<DiaryData>>
}