package com.mskwak.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.PlantData
import com.mskwak.data.source.PlantDao
import com.mskwak.domain.model.PlantModel
import com.mskwak.domain.repository.PlantRepository
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao
) : PlantRepository {

    override suspend fun addPlant(plant: PlantModel) {
        plantDao.insertPlant(plant as PlantData)
    }

    override suspend fun updatePlant(plant: PlantModel) {
        plantDao.updatePlant(plant as PlantData)
    }

    override suspend fun deletePlant(plant: PlantModel) {
        plantDao.deletePlant(plant as PlantData)
    }

    override fun observePlants(): LiveData<List<PlantModel>> {
        return plantDao.observePlants().map { it }
    }

    override fun observePlant(plantId: Int): LiveData<PlantModel> {
        return plantDao.observePlant(plantId).map { it }
    }

}