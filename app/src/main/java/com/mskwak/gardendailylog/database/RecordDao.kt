package com.mskwak.gardendailylog.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.gardendailylog.data.GardeningRecord

@Dao
interface RecordDao {

    @Insert
    suspend fun insertRecord(record: GardeningRecord)

    @Update
    suspend fun updateRecord(record: GardeningRecord)

    @Delete
    suspend fun deleteRecord(record: GardeningRecord)

    @Query("SELECT * FROM gardeningRecord WHERE plantId = :plantId")
    fun observeRecordsByPlantId(plantId: Int): LiveData<List<GardeningRecord>>

    @Query("SELECT * FROM gardeningRecord ORDER BY createdTime DESC")
    fun observeRecords(): LiveData<List<GardeningRecord>>
}