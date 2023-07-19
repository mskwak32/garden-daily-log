package com.mskwak.domain.usecase

import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.repository.PictureRepository
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.type.PlantListSortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantUseCase @Inject constructor(
    private val plantRepository: PlantRepository,
    private val diaryRepository: DiaryRepository,
    private val pictureRepository: PictureRepository,
    private val wateringUseCase: WateringUseCase
) {

    fun getPlantsWithSortOrder(sortOrder: PlantListSortOrder): Flow<List<Plant>> {
        return plantRepository.getPlants().map { list ->
            list.applySort(sortOrder)
        }.flowOn(Dispatchers.IO)
    }

    private fun List<Plant>.applySort(sortOrder: PlantListSortOrder): List<Plant> {
        return when(sortOrder) {
            PlantListSortOrder.CREATED_LATEST -> {
                sortedByDescending { plant -> plant.createdDate }
            }

            PlantListSortOrder.CREATED_OLDEST -> {
                sortedBy { plant -> plant.createdDate }
            }

            PlantListSortOrder.WATERING -> {
                sortedBy { plant -> wateringUseCase.getRemainWateringDate(plant) }
            }
        }
    }

    fun getPlantFlow(plantId: Int): Flow<Plant> {
        return plantRepository.getPlantFlow(plantId)
    }

    suspend fun getPlant(plantId: Int): Plant {
        return plantRepository.getPlant(plantId)
    }

    suspend fun addPlant(plant: Plant) {
        val id = plantRepository.addPlant(plant)
        wateringUseCase.setWateringAlarm(id, isActive = plant.wateringAlarm.onOff)
    }

    suspend fun updatePlant(plant: Plant) {
        plantRepository.updatePlant(plant)
        wateringUseCase.setWateringAlarm(plant.id, isActive = plant.wateringAlarm.onOff)
    }

    suspend fun deletePlant(plant: Plant) {
        diaryRepository.deleteDiariesByPlantId(plant.id)
        plant.pictureUri?.let { pictureRepository.deletePlantPicture(it) }
        plantRepository.deletePlant(plant)
        wateringUseCase.setWateringAlarm(plant.id, isActive = false)
    }

    suspend fun getPlantName(plantId: Int): String {
        return plantRepository.getPlantName(plantId)
    }

    suspend fun getPlantNames(): Map<Int, String> {
        return plantRepository.getPlantNames()
    }
}