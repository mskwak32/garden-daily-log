package com.mskwak.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mskwak.data.model.PlantData
import com.mskwak.data.source.FileDataSource
import com.mskwak.data.source.PlantDao
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.PlantRepository
import com.orhanobut.logger.Logger
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
    private val fileDataSource: FileDataSource
) : PlantRepository {

    override suspend fun addPlant(plant: Plant) {
        plantDao.insertPlant(PlantData(plant))
        Logger.d("add new plant")
    }

    override suspend fun updatePlant(plant: Plant) {
        plantDao.updatePlant(PlantData(plant))
        Logger.d("update plant id= ${plant.id}")
    }

    override suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(PlantData(plant))
        plant.pictureUri?.let { deletePlantPicture(it) }
        Logger.d("delete plant id= ${plant.id}")
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

    override suspend fun savePlantPicture(bitmap: Bitmap): Uri {
        val uri = fileDataSource.savePicture(FileDataSource.PLANT_PICTURE_DIR, bitmap)
        Logger.d("save picture: ${uri.path}")
        return uri
    }

    override suspend fun deletePlantPicture(uri: Uri) {
        fileDataSource.deletePicture(uri)
        Logger.d("delete picture: ${uri.path}")
    }

}