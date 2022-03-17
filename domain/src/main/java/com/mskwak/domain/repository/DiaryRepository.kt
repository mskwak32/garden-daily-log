package com.mskwak.domain.repository

import androidx.lifecycle.LiveData
import com.mskwak.domain.model.Diary

interface DiaryRepository {

    suspend fun addDiary(diary: Diary)
    suspend fun updateDiary(diary: Diary)
    suspend fun deleteDiary(diary: Diary)
    fun getDiariesByPlantId(plantId: Int, limit: Int): LiveData<List<Diary>>
    suspend fun getDiary(id: Int): Diary
    fun getDiaryLiveData(id: Int): LiveData<Diary>
    suspend fun deleteDiariesByPlantId(plantId: Int)
    fun getDiaries(year: Int, month: Int, plantId: Int?): LiveData<List<Diary>>
}