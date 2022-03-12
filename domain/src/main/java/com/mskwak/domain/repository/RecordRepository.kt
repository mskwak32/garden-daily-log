package com.mskwak.domain.repository

import androidx.lifecycle.LiveData
import com.mskwak.domain.model.Record

interface RecordRepository {

    suspend fun addRecord(record: Record)
    suspend fun updateRecord(record: Record)
    suspend fun deleteRecord(record: Record)
    fun observeRecordsByPlantId(plantId: Int): LiveData<List<Record>>
    fun observeRecods(): LiveData<List<Record>>
    suspend fun getRecordById(id: Int): Record
    fun observeRecordById(id: Int): LiveData<Record>
    suspend fun deleteRecordsByPlantId(plantId: Int)
}