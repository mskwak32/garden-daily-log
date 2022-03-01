package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.RecordData

@Dao
interface RecordDao {

    @Insert
    suspend fun insertRecord(record: RecordData)

    @Update
    suspend fun updateRecord(record: RecordData)

    @Delete
    suspend fun deleteRecord(record: RecordData)

    @Query("SELECT * FROM gardeningRecord WHERE plantId = :plantId")
    fun observeRecordsByPlantId(plantId: Int): LiveData<List<RecordData>>

    @Query("SELECT * FROM gardeningRecord ORDER BY createdTime DESC")
    fun observeRecords(): LiveData<List<RecordData>>
}