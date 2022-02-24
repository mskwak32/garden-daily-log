package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.RecordImpl

@Dao
interface RecordDao {

    @Insert
    suspend fun insertRecord(record: RecordImpl)

    @Update
    suspend fun updateRecord(record: RecordImpl)

    @Delete
    suspend fun deleteRecord(record: RecordImpl)

    @Query("SELECT * FROM gardeningRecord WHERE plantId = :plantId")
    fun observeRecordsByPlantId(plantId: Int): LiveData<List<RecordImpl>>

    @Query("SELECT * FROM gardeningRecord ORDER BY createdTime DESC")
    fun observeRecords(): LiveData<List<RecordImpl>>
}