package com.mskwak.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mskwak.data.model.PlantImpl

@Dao
interface PlantDao {
    @Insert
    suspend fun insertPlant(plant: PlantImpl)

    @Update
    suspend fun updatePlant(plant: PlantImpl)

    @Delete
    suspend fun deletePlant(plant: PlantImpl)

    @Query("SELECT * FROM plant")
    fun observePlants(): LiveData<List<PlantImpl>>

    @Query("SELECT * FROM plant WHERE id = :plantId")
    fun observePlant(plantId: Int): LiveData<PlantImpl>
}