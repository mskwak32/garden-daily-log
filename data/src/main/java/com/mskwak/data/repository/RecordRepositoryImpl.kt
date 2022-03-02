package com.mskwak.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.RecordData
import com.mskwak.data.source.RecordDao
import com.mskwak.domain.model.Record
import com.mskwak.domain.repository.RecordRepository
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao
) : RecordRepository {
    override suspend fun addRecord(record: Record) {
        recordDao.insertRecord(RecordData(record))
    }

    override suspend fun updateRecord(record: Record) {
        recordDao.updateRecord(RecordData(record))
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.deleteRecord(RecordData(record))
    }

    override fun observeRecordsByPlantId(plantId: Int): LiveData<List<Record>> {
        return recordDao.observeRecordsByPlantId(plantId).map { it }
    }

    override fun observeRecods(): LiveData<List<Record>> {
        return recordDao.observeRecords().map { it }
    }
}