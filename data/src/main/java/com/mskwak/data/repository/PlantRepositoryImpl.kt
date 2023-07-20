package com.mskwak.data.repository

import com.mskwak.data.source.local.PlantDao
import com.mskwak.data.toPlant
import com.mskwak.data.toPlantData
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.PlantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao
) : PlantRepository {

    override suspend fun addPlant(plant: Plant): Int = withContext(Dispatchers.IO) {
        val id = plantDao.insertPlant(plant.toPlantData()).toInt()
        Timber.d("add new plant id= $id")
        id
    }

    override suspend fun updatePlant(plant: Plant) = withContext(Dispatchers.IO) {
        plantDao.updatePlant(plant.toPlantData())
        Timber.d("update plant id= ${plant.id}")
    }

    override suspend fun deletePlant(plant: Plant) = withContext(Dispatchers.IO) {
        plantDao.deletePlant(plant.toPlantData())
        Timber.d("delete plant id= ${plant.id}")
    }

    override suspend fun getPlant(plantId: Int): Plant = withContext(Dispatchers.IO) {
        plantDao.getPlant(plantId).toPlant()
    }

    override fun getPlants(): Flow<List<Plant>> {
        return plantDao.getPlants().map { list ->
            list.map { it.toPlant() }
        }
    }

    override fun getPlantFlow(plantId: Int): Flow<Plant> {
        return plantDao.getPlantFlow(plantId).map { it.toPlant() }
    }

    override suspend fun getPlantName(plantId: Int): String = withContext(Dispatchers.IO) {
        plantDao.getPlantName(plantId)
    }

    override suspend fun getPlantNames(): Map<Int, String> = withContext(Dispatchers.IO) {
        plantDao.getPlantNames()
    }

    override suspend fun getPlantIdWithAlarmList(): Map<Int, Boolean> =
        withContext(Dispatchers.IO) {
            plantDao.getPlantIdWithAlarmList()
        }

}