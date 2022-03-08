package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.PlantData
import java.time.LocalDate

@Dao
interface PlantDao {
    @Insert
    suspend fun insertPlant(plant: PlantData)

    @Update
    suspend fun updatePlant(plant: PlantData)

    @Delete
    suspend fun deletePlant(plant: PlantData)

    @Query("SELECT * FROM plant WHERE id = :plantId")
    suspend fun getPlant(plantId: Int): PlantData

    @Query("SELECT * FROM plant")
    fun observePlants(): LiveData<List<PlantData>>

    @Query("SELECT * FROM plant WHERE id = :plantId")
    fun observePlant(plantId: Int): LiveData<PlantData>

    @Query("UPDATE plant SET lastWateringDate = :date WHERE id = :plantId")
    suspend fun updateLastWatering(date: LocalDate, plantId: Int)

    @Query("UPDATE plant SET onOff = :isActive WHERE id = :plantId")
    suspend fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int)

    @Query("SELECT name FROM plant WHERE id = :plantId")
    suspend fun getPlantName(plantId: Int): String
}