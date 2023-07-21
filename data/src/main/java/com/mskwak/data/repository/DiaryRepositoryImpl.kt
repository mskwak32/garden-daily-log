package com.mskwak.data.repository

import com.mskwak.data.source.local.DiaryDao
import com.mskwak.data.toDiary
import com.mskwak.data.toDiaryData
import com.mskwak.domain.model.Diary
import com.mskwak.domain.repository.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao
) : DiaryRepository {

    override suspend fun addDiary(diary: Diary) = withContext(Dispatchers.IO) {
        diaryDao.insertDiary(diary.toDiaryData())
        Timber.d("add new diary")
    }

    override suspend fun updateDiary(diary: Diary) = withContext(Dispatchers.IO) {
        diaryDao.updateDiary(diary.toDiaryData())
        Timber.d("update diary id= ${diary.id}")
    }

    override suspend fun deleteDiary(diary: Diary) = withContext(Dispatchers.IO) {
        diaryDao.deleteDiary(diary.toDiaryData())
        Timber.d("delete diary id= ${diary.id}")
    }

    override suspend fun deleteDiariesByPlantId(plantId: Int) = withContext(Dispatchers.IO) {
        diaryDao.getDiariesByPlantId(plantId).forEach { diary ->
            diaryDao.deleteDiary(diary)
        }
    }

    override fun getDiariesByPlantId(plantId: Int, limit: Int): Flow<List<Diary>> {
        return diaryDao.getDiariesByPlantId(plantId, limit).map { list ->
            list.map { it.toDiary() }
        }
    }

    override fun getDiary(id: Int): Flow<Diary> {
        return diaryDao.getDiary(id).map { it.toDiary() }
    }

    override fun getDiaries(year: Int, month: Int, plantId: Int?): Flow<List<Diary>> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = LocalDate.of(year, month, startDate.lengthOfMonth())
        return if (plantId == null) {
            diaryDao.getDiaries(startDate, endDate).map { list ->
                list.map { it.toDiary() }
            }
        } else {
            diaryDao.getDiariesByPlantId(plantId, startDate, endDate).map { list ->
                list.map { it.toDiary() }
            }
        }
    }
}