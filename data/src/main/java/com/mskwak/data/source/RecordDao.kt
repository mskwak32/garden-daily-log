package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.RecordModel

@Dao
interface RecordDao {

    @Insert
    suspend fun insertRecord(record: RecordModel)

    @Update
    suspend fun updateRecord(record: RecordModel)

    @Delete
    suspend fun deleteRecord(record: RecordModel)

    @Query("SELECT * FROM gardeningRecord WHERE plantId = :plantId")
    fun observeRecordsByPlantId(plantId: Int): LiveData<List<RecordModel>>

    @Query("SELECT * FROM gardeningRecord ORDER BY createdTime DESC")
    fun observeRecords(): LiveData<List<RecordModel>>
}