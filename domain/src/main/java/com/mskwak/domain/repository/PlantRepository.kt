package com.mskwak.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.mskwak.domain.model.Plant
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface PlantRepository {

    suspend fun addPlant(plant: Plant): Int
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant)
    suspend fun getPlant(plantId: Int): Plant
    fun getPlants(): Flow<List<Plant>>
    fun getPlantFlow(plantId: Int): Flow<Plant>
    suspend fun savePlantPicture(bitmap: Bitmap): Uri
    suspend fun deletePlantPicture(uri: Uri)
    suspend fun updateLastWateringDate(date: LocalDate, plantId: Int)
    suspend fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int)
    suspend fun getPlantName(plantId: Int): String
    suspend fun getPlantNames(): Map<Int, String>
    suspend fun getPlantIdWithAlarmList(): Map<Int, Boolean>
}