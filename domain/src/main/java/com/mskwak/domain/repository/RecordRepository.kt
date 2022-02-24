package com.mskwak.domain.repository

import androidx.lifecycle.LiveData
import com.mskwak.domain.model.RecordModel

interface RecordRepository {

    suspend fun addRecord(record: RecordModel)
    suspend fun updateRecord(record: RecordModel)
    suspend fun deleteRecord(record: RecordModel)
    fun observeRecordsByPlantId(plantId: Int): LiveData<List<RecordModel>>
    fun observeRecods(): LiveData<List<RecordModel>>
}