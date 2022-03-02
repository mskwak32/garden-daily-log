package com.mskwak.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.mskwak.domain.model.Plant
import com.mskwak.domain.model.Record
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.repository.RecordRepository
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

class GardenUseCase(
    private val plantRepository: PlantRepository,
    private val recordRepository: RecordRepository
) {

    fun getPlantsWithSortOrder(sortOrder: PlantListSortOrder): LiveData<List<Plant>> {
        val plantsLiveData = plantRepository.observePlants()
        return plantsLiveData.switchMap { list ->
            liveData {
                emit(list.applySort(sortOrder))
            }
        }
    }

    private suspend fun List<Plant>.applySort(sortOrder: PlantListSortOrder): List<Plant> =
        withContext(Dispatchers.Default) {
            when (sortOrder) {
                PlantListSortOrder.CREATED_LATEST -> {
                    sortedByDescending { plant -> plant.createdDate }
                }
                PlantListSortOrder.CREATED_OLDEST -> {
                    sortedBy { plant -> plant.createdDate }
                }
                PlantListSortOrder.WATERING -> {
                    sortedBy { plant -> getRemainWateringDate(plant) }
                }
            }
        }

    fun getPlantWithRecords(plantId: Int): Pair<LiveData<Plant>, LiveData<List<Record>>> {
        val plantLiveData = plantRepository.observePlant(plantId)
        val recordsLiveData = recordRepository.observeRecordsByPlantId(plantId)
        return Pair(plantLiveData, recordsLiveData)
    }

    fun getPlant(plantId: Int, scope: CoroutineScope, onComplete: (plant: Plant) -> Unit) {
        scope.launch {
            val deferred = async(Dispatchers.IO) {
                plantRepository.getPlant(plantId)
            }
            onComplete(deferred.await())
        }
    }

    fun addPlant(plant: Plant, scope: CoroutineScope, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            plantRepository.addPlant(plant)
            scope.launch {
                onComplete.invoke()
            }
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

    fun deletePlant(plant: Plant) {
        CoroutineScope(Dispatchers.IO).launch {
            plantRepository.deletePlant(plant)
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
        val plantDate = plant.createdDate

        if (!today.isAfter(plantDate)) {
            return Period.ofDays(0)
        }

        return Period.between(plantDate, today)
    }

}