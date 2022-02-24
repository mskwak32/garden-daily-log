package com.mskwak.domain.usecase

import androidx.lifecycle.LiveData
import com.mskwak.domain.model.PlantModel
import com.mskwak.domain.model.RecordModel
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.repository.RecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GardenUseCase(
    private val plantRepository: PlantRepository,
    private val recordRepository: RecordRepository
) {
    fun getPlants(): LiveData<List<PlantModel>> {
        return plantRepository.observePlants()
    }

    fun getPlant(
        plantId: Int,
        mapKeyPlant: String,
        mapKeyRecord: String
    ): Map<String, LiveData<*>> {
        val plantLiveData = plantRepository.observePlant(plantId)
        val recordsLiveData = recordRepository.observeRecordsByPlantId(plantId)
        return mapOf<String, LiveData<*>>(
            Pair(mapKeyPlant, plantLiveData),
            Pair(mapKeyRecord, recordsLiveData)
        )
    }

    fun addPlant(plant: PlantModel) {
        CoroutineScope(Dispatchers.IO).launch {
            plantRepository.addPlant(plant)
        }
    }

    fun updatePlant(plant: PlantModel) {
        CoroutineScope(Dispatchers.IO).launch {
            plantRepository.updatePlant(plant)
        }
    }

    fun deletePlant(plant: PlantModel) {
        CoroutineScope(Dispatchers.IO).launch {
            plantRepository.deletePlant(plant)
        }
    }

    fun getRecords(): LiveData<List<RecordModel>> {
        return recordRepository.observeRecods()
    }

    fun addRecord(record: RecordModel) {
        CoroutineScope(Dispatchers.IO).launch {
            recordRepository.addRecord(record)
        }
    }

    fun updateRecord(record: RecordModel) {
        CoroutineScope(Dispatchers.IO).launch {
            recordRepository.updateRecord(record)
        }
    }

    fun deleteRecord(record: RecordModel) {
        CoroutineScope(Dispatchers.IO).launch {
            recordRepository.deleteRecord(record)
        }
    }
}