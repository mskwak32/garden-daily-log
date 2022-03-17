package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.DiaryData
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
    fun getDiariesByPlantId(plantId: Int, limit: Int): LiveData<List<DiaryData>>

    @Query("SELECT * FROM diary WHERE plantId = :plantId AND (createdDate BETWEEN :startDate AND :endDate)")
    fun getDiariesByPlantId(
        plantId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): LiveData<List<DiaryData>>

    @Query("SELECT * FROM diary WHERE id = :id")
    suspend fun getDiary(id: Int): DiaryData

    @Query("SELECT * FROM diary WHERE id = :id")
    fun getDiaryLiveData(id: Int): LiveData<DiaryData>

    @Query("SELECT * FROM diary WHERE createdDate BETWEEN :startDate AND :endDate")
    fun getDiaries(startDate: LocalDate, endDate: LocalDate): LiveData<List<DiaryData>>
}