package com.mskwak.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.mskwak.data.model.PlantData
import com.mskwak.data.source.local.FileDataSource
import com.mskwak.data.source.local.PlantDao
import com.mskwak.domain.di.IoDispatcher
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.PlantRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
    private val fileDataSource: FileDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PlantRepository {

    override suspend fun addPlant(plant: Plant): Int = withContext(ioDispatcher) {
        val id = plantDao.insertPlant(PlantData(plant)).toInt()
        Timber.d("add new plant id= $id")
        id
    }

    override suspend fun updatePlant(plant: Plant) = withContext(ioDispatcher) {
        plantDao.updatePlant(PlantData(plant))
        Timber.d("update plant id= ${plant.id}")
    }

    override suspend fun deletePlant(plant: Plant) = withContext(ioDispatcher) {
        plantDao.deletePlant(PlantData(plant))
        plant.pictureUri?.let { deletePlantPicture(it) }
        Timber.d("delete plant id= ${plant.id}")
    }

    override suspend fun getPlant(plantId: Int): Plant = withContext(ioDispatcher) {
        plantDao.getPlant(plantId)
    }

    override fun getPlants(): Flow<List<Plant>> {
        return plantDao.getPlants().map { it }
    }

    override fun getPlantFlow(plantId: Int): Flow<Plant> {
        return plantDao.getPlantFlow(plantId).map { it }
    }

    override suspend fun savePlantPicture(bitmap: Bitmap): Uri = withContext(ioDispatcher) {
        fileDataSource.savePicture(FileDataSource.PLANT_PICTURE_DIR, bitmap)
    }

    override suspend fun deletePlantPicture(uri: Uri) = withContext(ioDispatcher) {
        fileDataSource.deletePicture(uri)
    }

    override suspend fun updateLastWateringDate(date: LocalDate, plantId: Int) =
        withContext(ioDispatcher) {
            plantDao.updateLastWatering(date, plantId)
        }

    override suspend fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int) =
        withContext(ioDispatcher) {
            plantDao.updateWateringAlarmOnOff(isActive, plantId)
        }

    override suspend fun getPlantName(plantId: Int): String = withContext(ioDispatcher) {
        plantDao.getPlantName(plantId)
    }

    override suspend fun getPlantNames(): Map<Int, String> = withContext(ioDispatcher) {
        plantDao.getPlantNames()
    }

    override suspend fun getPlantIdWithAlarmList(): Map<Int, Boolean> = withContext(ioDispatcher) {
        plantDao.getPlantIdWithAlarmList()
    }

}