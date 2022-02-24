package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.PlantData

@Dao
interface PlantDao {
    @Insert
    suspend fun insertPlant(plant: PlantData)

    @Update
    suspend fun updatePlant(plant: PlantData)

    @Delete
    suspend fun deletePlant(plant: PlantData)

    @Query("SELECT * FROM plant")
    fun observePlants(): LiveData<List<PlantData>>

    @Query("SELECT * FROM plant WHERE id = :plantId")
    fun observePlant(plantId: Int): LiveData<PlantData>
}