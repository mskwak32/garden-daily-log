package com.mskwak.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.PlantData
import com.mskwak.data.source.PlantDao
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.PlantRepository
import com.orhanobut.logger.Logger
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao
) : PlantRepository {

    override suspend fun addPlant(plant: Plant) {
        plantDao.insertPlant(PlantData(plant))
        Logger.d("plant id= ${plant.id}")
    }

    override suspend fun updatePlant(plant: Plant) {
        plantDao.updatePlant(PlantData(plant))
        Logger.d("plant id= ${plant.id}")
    }

    override suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(PlantData(plant))
        Logger.d("plant id= ${plant.id}")
    }

    override suspend fun getPlant(plantId: Int): Plant {
        return plantDao.getPlant(plantId)
    }

    override fun observePlants(): LiveData<List<Plant>> {
        return plantDao.observePlants().map { it }
    }

    override fun observePlant(plantId: Int): LiveData<Plant> {
        return plantDao.observePlant(plantId).map { it }
    }

}