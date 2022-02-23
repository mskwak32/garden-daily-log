package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.Plant

@Dao
interface PlantDao {
    @Insert
    suspend fun insertPlant(plant: Plant)

    @Update
    suspend fun updatePlant(plant: Plant)

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Query("SELECT * FROM plant")
    fun observePlants(): LiveData<List<Plant>>

    @Query("SELECT * FROM plant WHERE id = :plantId")
    fun observePlant(plantId: Int): LiveData<Plant>
}