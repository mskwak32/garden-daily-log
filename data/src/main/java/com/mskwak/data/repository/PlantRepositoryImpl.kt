package com.mskwak.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.PlantData
import com.mskwak.data.source.local.FileDataSource
import com.mskwak.data.source.local.PlantDao
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.PlantRepository
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
    private val fileDataSource: FileDataSource
) : PlantRepository {

    override suspend fun addPlant(plant: Plant): Int {
        val id = plantDao.insertPlant(PlantData(plant)).toInt()
        Timber.d("add new plant id= $id")
        return id
    }

    override suspend fun updatePlant(plant: Plant) {
        plantDao.updatePlant(PlantData(plant))
        Timber.d("update plant id= ${plant.id}")
    }

    override suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(PlantData(plant))
        plant.pictureUri?.let { deletePlantPicture(it) }
        Timber.d("delete plant id= ${plant.id}")
    }

    override suspend fun getPlant(plantId: Int): Plant {
        return plantDao.getPlant(plantId)
    }

    override fun getPlants(): LiveData<List<Plant>> {
        return plantDao.getPlants().map { it }
    }

    override fun getPlantLiveData(plantId: Int): LiveData<Plant> {
        return plantDao.getPlantLiveData(plantId).map { it }
    }

    override suspend fun savePlantPicture(bitmap: Bitmap): Uri {
        return fileDataSource.savePicture(FileDataSource.PLANT_PICTURE_DIR, bitmap)
    }

    override suspend fun deletePlantPicture(uri: Uri) {
        fileDataSource.deletePicture(uri)
    }

    override suspend fun updateLastWateringDate(date: LocalDate, plantId: Int) {
        plantDao.updateLastWatering(date, plantId)
    }

    override suspend fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int) {
        plantDao.updateWateringAlarmOnOff(isActive, plantId)
    }

    override suspend fun getPlantName(plantId: Int): String {
        return plantDao.getPlantName(plantId)
    }

    override suspend fun getPlantNames(): Map<Int, String> {
        return plantDao.getPlantNames()
    }

    override suspend fun getPlantIdWithAlarmList(): Map<Int, Boolean> {
        return plantDao.getPlantIdWithAlarmList()
    }

}