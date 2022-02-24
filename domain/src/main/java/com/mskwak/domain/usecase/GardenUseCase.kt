package com.mskwak.domain.usecase

import androidx.lifecycle.LiveData
import com.mskwak.domain.model.Plant
import com.mskwak.domain.model.Record
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.repository.RecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

class GardenUseCase(
    private val plantRepository: PlantRepository,
    private val recordRepository: RecordRepository
) {
    fun getPlants(): LiveData<List<Plant>> {
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

    fun addPlant(plant: Plant) {
        CoroutineScope(Dispatchers.IO).launch {
            plantRepository.addPlant(plant)
        }
    }

    fun updatePlant(plant: Plant, scope: CoroutineScope, onComplete: () -> Unit) {
        scope.launch {
            val deferred = async(Dispatchers.IO) {
                plantRepository.updatePlant(plant)
            }
            deferred.await()
            onComplete.invoke()
        }
    }

    fun deletePlant(plant: Plant, scope: CoroutineScope, onComplete: () -> Unit) {
        scope.launch {
            val deferred = async(Dispatchers.IO) {
                plantRepository.deletePlant(plant)
            }
            deferred.await()
            onComplete.invoke()
        }
    }

    fun getRecords(): LiveData<List<Record>> {
        return recordRepository.observeRecods()
    }

    fun addRecord(record: Record) {
        CoroutineScope(Dispatchers.IO).launch {
            recordRepository.addRecord(record)
        }
    }

    fun updateRecord(record: Record, scope: CoroutineScope, onComplete: () -> Unit) {
        scope.launch {
            val deferred = async(Dispatchers.IO) {
                recordRepository.updateRecord(record)
            }
            deferred.await()
            onComplete.invoke()
        }
    }

    fun deleteRecord(record: Record, scope: CoroutineScope, onComplete: () -> Unit) {
        scope.launch {
            val deferred = async(Dispatchers.IO) {
                recordRepository.deleteRecord(record)
            }
            deferred.await()
            onComplete.invoke()
        }
    }

    fun getRemainWateringDate(plant: Plant): Int {
        val today = LocalDate.now()
        val nextDate = plant.lastWateringDate.plusDays(plant.waterPeriod.toLong())

        if (!today.isBefore(nextDate)) {
            return 0
        }

        return ChronoUnit.DAYS.between(today, nextDate).toInt()
    }

    fun getDaysFromLastWatering(plant: Plant): Int {
        val today = LocalDate.now()

        if (!today.isAfter(plant.lastWateringDate)) {
            return 0
        }

        return ChronoUnit.DAYS.between(plant.lastWateringDate, today).toInt()
    }

    fun getDaysFromPlant(plant: Plant): Period {
        val today = LocalDate.now()
        val plantDate = plant.createdDate.toLocalDate()

        if (!today.isAfter(plantDate)) {
            return Period.ofDays(0)
        }

        return Period.between(plantDate, today)
    }

}