package com.mskwak.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.PlantImpl
import com.mskwak.data.source.PlantDao
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.PlantRepository
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao
) : PlantRepository {

    override suspend fun addPlant(plant: Plant) {
        plantDao.insertPlant(plant as PlantImpl)
    }

    override suspend fun updatePlant(plant: Plant) {
        plantDao.updatePlant(plant as PlantImpl)
    }

    override suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(plant as PlantImpl)
    }

    override fun observePlants(): LiveData<List<Plant>> {
        return plantDao.observePlants().map { it }
    }

    override fun observePlant(plantId: Int): LiveData<Plant> {
        return plantDao.observePlant(plantId).map { it }
    }

}