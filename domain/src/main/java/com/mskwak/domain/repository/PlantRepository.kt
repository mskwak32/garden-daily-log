package com.mskwak.domain.repository

import com.mskwak.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {

    suspend fun addPlant(plant: Plant): Int
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant)
    fun getPlants(): Flow<List<Plant>>
    fun getPlant(plantId: Int): Flow<Plant>
    suspend fun getPlantName(plantId: Int): String
    suspend fun getPlantNames(): Map<Int, String>
    suspend fun getPlantIdWithAlarmList(): Map<Int, Boolean>
}