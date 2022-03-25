package com.mskwak.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.PlantData
import java.time.LocalDate

@Dao
interface PlantDao {
    @Insert
    suspend fun insertPlant(plant: PlantData): Long

    @Update
    suspend fun updatePlant(plant: PlantData)

    @Delete
    suspend fun deletePlant(plant: PlantData)

    @Query("SELECT * FROM plant WHERE id = :plantId")
    suspend fun getPlant(plantId: Int): PlantData

    @Query("SELECT * FROM plant")
    fun getPlants(): LiveData<List<PlantData>>

    @Query("SELECT * FROM plant WHERE id = :plantId")
    fun getPlantLiveData(plantId: Int): LiveData<PlantData>

    @Query("UPDATE plant SET lastWateringDate = :date WHERE id = :plantId")
    suspend fun updateLastWatering(date: LocalDate, plantId: Int)

    @Query("UPDATE plant SET onOff = :isActive WHERE id = :plantId")
    suspend fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int)

    @Query("SELECT name FROM plant WHERE id = :plantId")
    suspend fun getPlantName(plantId: Int): String

    @MapInfo(keyColumn = "plantId", valueColumn = "plantName")
    @Query("SELECT id AS plantId, name AS plantName FROM plant")
    suspend fun getPlantNames(): Map<Int, String>

    @MapInfo(keyColumn = "plantId", valueColumn = "AlarmOnOff")
    @Query("SELECT id AS plantId, onOff AS AlarmOnOff FROM plant")
    suspend fun getPlantIdWithAlarmList(): Map<Int, Boolean>
}