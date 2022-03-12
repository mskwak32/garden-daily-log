package com.mskwak.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.DiaryData
import com.mskwak.data.source.DiaryDao
import com.mskwak.data.source.FileDataSource
import com.mskwak.domain.model.Diary
import com.mskwak.domain.repository.DiaryRepository
import timber.log.Timber
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao,
    private val fileDataSource: FileDataSource
) : DiaryRepository {
    override suspend fun addDiary(diary: Diary) {
        diaryDao.insertDiary(DiaryData(diary))
        Timber.d("add new diary")
    }

    override suspend fun updateDiary(diary: Diary) {
        diaryDao.updateDiary(DiaryData(diary))
        Timber.d("update diary id= ${diary.id}")
    }

    override suspend fun deleteDiary(diary: Diary) {
        deleteDiaryPictures(diary)
        diaryDao.deleteDiary(DiaryData(diary))
        Timber.d("delete diary id= ${diary.id}")
    }

    override suspend fun deleteDiariesByPlantId(plantId: Int) {
        diaryDao.getDiariesByPlantId(plantId).forEach { diary ->
            deleteDiary(diary)
        }
    }

    override fun observeDiariesByPlantId(plantId: Int): LiveData<List<Diary>> {
        return diaryDao.observeDiariesByPlantId(plantId).map { it }
    }

    override fun observeDiaries(): LiveData<List<Diary>> {
        return diaryDao.observeDiaries().map { it }
    }

    override suspend fun getDiaryById(id: Int): Diary {
        return diaryDao.getDiaryById(id)
    }

    override fun observeDiaryById(id: Int): LiveData<Diary> {
        return diaryDao.observeDiaryById(id).map { it }
    }

    private fun deleteDiaryPictures(diary: Diary) {
        diary.pictureList?.forEach {
            fileDataSource.deletePicture(it)
        }
    }
}