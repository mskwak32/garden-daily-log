package com.mskwak.domain.repository

import androidx.lifecycle.LiveData
import com.mskwak.domain.model.PlantModel

interface PlantRepository {

    suspend fun addPlant(plant: PlantModel)
    suspend fun updatePlant(plant: PlantModel)
    suspend fun deletePlant(plant: PlantModel)
    fun observePlants(): LiveData<List<PlantModel>>
    fun observePlant(plantId: Int): LiveData<PlantModel>
}