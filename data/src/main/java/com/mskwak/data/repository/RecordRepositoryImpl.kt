package com.mskwak.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.RecordData
import com.mskwak.data.source.FileDataSource
import com.mskwak.data.source.RecordDao
import com.mskwak.domain.model.Record
import com.mskwak.domain.repository.RecordRepository
import com.orhanobut.logger.Logger
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao,
    private val fileDataSource: FileDataSource
) : RecordRepository {
    override suspend fun addRecord(record: Record) {
        recordDao.insertRecord(RecordData(record))
        Logger.d("add new record")
    }

    override suspend fun updateRecord(record: Record) {
        recordDao.updateRecord(RecordData(record))
        Logger.d("update record id= ${record.id}")
    }

    override suspend fun deleteRecord(record: Record) {
        deleteRecordPictures(record)
        recordDao.deleteRecord(RecordData(record))
        Logger.d("delete record id= ${record.id}")
    }

    override suspend fun deleteRecordsByPlantId(plantId: Int) {
        recordDao.getRecordsByPlantId(plantId).forEach { record ->
            deleteRecord(record)
        }
    }

    override fun observeRecordsByPlantId(plantId: Int): LiveData<List<Record>> {
        return recordDao.observeRecordsByPlantId(plantId).map { it }
    }

    override fun observeRecods(): LiveData<List<Record>> {
        return recordDao.observeRecords().map { it }
    }

    override suspend fun getRecordById(id: Int): Record {
        return recordDao.getRecordById(id)
    }

    private fun deleteRecordPictures(record: Record) {
        record.pictureList?.forEach {
            fileDataSource.deletePicture(it)
        }
    }
}