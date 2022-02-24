package com.mskwak.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.RecordData
import com.mskwak.data.source.RecordDao
import com.mskwak.domain.model.RecordModel
import com.mskwak.domain.repository.RecordRepository
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao
) : RecordRepository {
    override suspend fun addRecord(record: RecordModel) {
        recordDao.insertRecord(record as RecordData)
    }

    override suspend fun updateRecord(record: RecordModel) {
        recordDao.updateRecord(record as RecordData)
    }

    override suspend fun deleteRecord(record: RecordModel) {
        recordDao.deleteRecord(record as RecordData)
    }

    override fun observeRecordsByPlantId(plantId: Int): LiveData<List<RecordModel>> {
        return recordDao.observeRecordsByPlantId(plantId).map { it }
    }

    override fun observeRecods(): LiveData<List<RecordModel>> {
        return recordDao.observeRecords().map { it }
    }
}