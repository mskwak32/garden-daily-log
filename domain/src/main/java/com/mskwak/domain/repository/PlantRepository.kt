package com.mskwak.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import com.mskwak.domain.model.Plant
import java.time.LocalDate

interface PlantRepository {

    suspend fun addPlant(plant: Plant)
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant)
    suspend fun getPlant(plantId: Int): Plant
    fun observePlants(): LiveData<List<Plant>>
    fun observePlant(plantId: Int): LiveData<Plant>
    suspend fun savePlantPicture(bitmap: Bitmap): Uri
    suspend fun deletePlantPicture(uri: Uri)
    suspend fun updateLastWateringDate(date: LocalDate, plantId: Int)
    suspend fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int)
    suspend fun getPlantName(plantId: Int): String
}