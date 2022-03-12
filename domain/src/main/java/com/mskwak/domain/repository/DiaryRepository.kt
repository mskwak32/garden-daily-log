package com.mskwak.domain.repository

import androidx.lifecycle.LiveData
import com.mskwak.domain.model.Diary

interface DiaryRepository {

    suspend fun addDiary(diary: Diary)
    suspend fun updateDiary(diary: Diary)
    suspend fun deleteDiary(diary: Diary)
    fun observeDiariesByPlantId(plantId: Int): LiveData<List<Diary>>
    fun observeDiaries(): LiveData<List<Diary>>
    suspend fun getDiaryById(id: Int): Diary
    fun observeDiaryById(id: Int): LiveData<Diary>
    suspend fun deleteDiariesByPlantId(plantId: Int)
}