package com.mskwak.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
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
    private val recordRepository: RecordRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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
            val deferred = async(ioDispatcher) {
                plantRepository.getPlant(plantId)
            }
            onComplete(deferred.await())
        }
    }

    suspend fun addPlant(plant: Plant) = withContext(ioDispatcher) {
        plantRepository.addPlant(plant)
    }

    suspend fun updatePlant(plant: Plant) = withContext(ioDispatcher) {
        plantRepository.updatePlant(plant)
    }

    fun deletePlant(plant: Plant) {
        CoroutineScope(ioDispatcher).launch {
            plantRepository.deletePlant(plant)
        }
    }

    fun getRecords(): LiveData<List<Record>> {
        return recordRepository.observeRecods()
    }

    fun addRecord(record: Record) {
        CoroutineScope(ioDispatcher).launch {
            recordRepository.addRecord(record)
        }
    }

    fun updateRecord(record: Record, scope: CoroutineScope, onComplete: () -> Unit) {
        scope.launch {
            val deferred = async(ioDispatcher) {
                recordRepository.updateRecord(record)
            }
            deferred.await()
            onComplete.invoke()
        }
    }

    fun deleteRecord(record: Record, scope: CoroutineScope, onComplete: () -> Unit) {
        scope.launch {
            val deferred = async(ioDispatcher) {
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

    suspend fun savePicture(bitmap: Bitmap): Uri = withContext(ioDispatcher) {
        plantRepository.savePlantPicture(bitmap)
    }

    fun deletePicture(uri: Uri) {
        CoroutineScope(ioDispatcher).launch {
            plantRepository.deletePlantPicture(uri)
        }
    }

}