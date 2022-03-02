package com.mskwak.domain.repository

import androidx.lifecycle.LiveData
import com.mskwak.domain.model.Plant

interface PlantRepository {

    suspend fun addPlant(plant: Plant)
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant)
    suspend fun getPlant(plantId: Int): Plant
    fun observePlants(): LiveData<List<Plant>>
    fun observePlant(plantId: Int): LiveData<Plant>
}