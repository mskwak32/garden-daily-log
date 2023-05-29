package com.mskwak.data.repository

import com.mskwak.data.model.DiaryData
import com.mskwak.data.source.local.DiaryDao
import com.mskwak.data.source.local.FileDataSource
import com.mskwak.domain.model.Diary
import com.mskwak.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDate
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

    override fun getDiariesByPlantId(plantId: Int, limit: Int): Flow<List<Diary>> {
        return diaryDao.getDiariesByPlantId(plantId, limit).map { it }
    }

    override suspend fun getDiary(id: Int): Diary {
        return diaryDao.getDiary(id)
    }

    override fun getDiaryFlow(id: Int): Flow<Diary> {
        return diaryDao.getDiaryFlow(id).map { it }
    }

    private fun deleteDiaryPictures(diary: Diary) {
        diary.pictureList?.forEach {
            fileDataSource.deletePicture(it)
        }
    }

    override fun getDiaries(year: Int, month: Int, plantId: Int?): Flow<List<Diary>> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = LocalDate.of(year, month, startDate.lengthOfMonth())
        return if (plantId == null) {
            diaryDao.getDiaries(startDate, endDate).map { it }
        } else {
            diaryDao.getDiariesByPlantId(plantId, startDate, endDate).map { it }
        }
    }
}