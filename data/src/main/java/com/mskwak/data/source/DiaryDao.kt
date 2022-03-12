package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.DiaryData

@Dao
interface DiaryDao {

    @Insert
    suspend fun insertDiary(diary: DiaryData)

    @Update
    suspend fun updateDiary(diary: DiaryData)

    @Delete
    suspend fun deleteDiary(diary: DiaryData)

    @Query("SELECT * FROM diary WHERE plantId = :plantId ORDER BY createdDate DESC")
    fun observeDiariesByPlantId(plantId: Int): LiveData<List<DiaryData>>

    @Query("SELECT * FROM diary ORDER BY createdDate DESC")
    fun observeDiaries(): LiveData<List<DiaryData>>

    @Query("SELECT * FROM diary WHERE id = :id")
    suspend fun getDiaryById(id: Int): DiaryData

    @Query("SELECT * FROM diary WHERE id = :id")
    fun observeDiaryById(id: Int): LiveData<DiaryData>

    @Query("SELECT * FROM diary WHERE plantId = :plantId")
    suspend fun getDiariesByPlantId(plantId: Int): List<DiaryData>
}