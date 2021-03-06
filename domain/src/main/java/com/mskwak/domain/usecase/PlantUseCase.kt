package com.mskwak.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.repository.PlantRepository
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class PlantUseCase(
    private val plantRepository: PlantRepository,
    private val diaryRepository: DiaryRepository,
    private val wateringAlarmManager: WateringAlarmManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getPlantsWithSortOrder(sortOrder: PlantListSortOrder): LiveData<List<Plant>> {
        val plantsLiveData = plantRepository.getPlants()
        return plantsLiveData.switchMap { list ->
            liveData(context = ioDispatcher) {
                emit(list.applySort(sortOrder))
            }
        }
    }

    private fun List<Plant>.applySort(sortOrder: PlantListSortOrder): List<Plant> {
        return when (sortOrder) {
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

    fun getPlantLiveData(plantId: Int): LiveData<Plant> {
        return plantRepository.getPlantLiveData(plantId)
    }

    suspend fun getPlant(plantId: Int): Plant = withContext(ioDispatcher) {
        plantRepository.getPlant(plantId)
    }

    suspend fun addPlant(plant: Plant) = withContext(ioDispatcher) {
        val id = plantRepository.addPlant(plant)
        setWateringAlarm(id, isActive = plant.wateringAlarm.onOff)
    }

    suspend fun updatePlant(plant: Plant) = withContext(ioDispatcher) {
        plantRepository.updatePlant(plant)
        setWateringAlarm(plant.id, isActive = plant.wateringAlarm.onOff)
    }

    fun deletePlant(plant: Plant) {
        CoroutineScope(ioDispatcher).launch {
            diaryRepository.deleteDiariesByPlantId(plant.id)
            plantRepository.deletePlant(plant)
            setWateringAlarm(plant.id, isActive = false)
        }
    }

    suspend fun getPlantName(plantId: Int): String = withContext(ioDispatcher) {
        plantRepository.getPlantName(plantId)
    }

    suspend fun getPlantNames(): Map<Int, String> {
        return plantRepository.getPlantNames()
    }

    /**
     * ????????? ??????????????? ?????? ?????? ????????? ????????????????????? D+00
     * ????????? ??????????????? ?????? ?????? ?????? ??????????????? D-00
     * ???????????? ?????? ?????? D+00
     *
     * return Pair(d-day, isDateOver)
     */
    fun getDdayText(plant: Plant): Pair<String, Boolean> {
        return if (plant.waterPeriod == 0) {
            val text = String.format("D+%02d", getDaysFromLastWatering(plant))
            Pair(text, false)
        } else {
            val days = getRemainWateringDate(plant)
            val format = if (days >= 0) "D-%02d" else "D+%02d"
            val text = String.format(format, abs(days))
            Pair(text, days < 0)
        }
    }

    /**
     * ????????? ????????? ?????? ?????? ?????? ???) 2??? ??? = 2
     * ????????? ????????? ?????? ?????? ?????? ???) 2??? ??? = -2
     * ????????? ????????? ?????? ?????? 10000
     */
    fun getRemainWateringDate(plant: Plant): Int {
        if (plant.waterPeriod == 0) {
            return 10000
        }

        val today = LocalDate.now()
        val nextDate = plant.lastWateringDate.plusDays(plant.waterPeriod.toLong())

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

    fun deletePicture(vararg uri: Uri) {
        CoroutineScope(ioDispatcher).launch {
            uri.forEach { plantRepository.deletePlantPicture(it) }
        }
    }

    suspend fun wateringNow(plant: Plant) = withContext(ioDispatcher) {
        val date = LocalDate.now()
        plantRepository.updateLastWateringDate(date, plant.id)

        //????????? ????????????????????? ?????? ????????????????????? ?????? ?????????
        if (plant.wateringAlarm.onOff) {
            setWateringAlarm(plant.id, plant.wateringAlarm.onOff)
        }
    }

    fun updateWateringAlarmOnOff(plantId: Int, isActive: Boolean) {
        CoroutineScope(ioDispatcher).launch {
            plantRepository.updateWateringAlarmOnOff(isActive, plantId)

            setWateringAlarm(plantId, isActive)
        }
    }

    suspend fun setWateringAlarm(plantId: Int, isActive: Boolean) {
        if (isActive) {
            //?????? ????????? ?????? ????????? ???????????? ??????
            getNextAlarmDateTime(plantId)?.let { nextDateTime ->
                wateringAlarmManager.setWateringAlarm(plantId, nextDateTime)
            }
        } else {
            wateringAlarmManager.cancelWateringAlarm(plantId)
        }
    }

    private suspend fun getNextAlarmDateTime(plantId: Int): LocalDateTime? =
        withContext(ioDispatcher) {
            val plant = getPlant(plantId)
            if (plant.waterPeriod == 0) return@withContext null

            val nextDate = plant.lastWateringDate.plusDays(plant.waterPeriod.toLong())
            var nextDateTime = LocalDateTime.of(nextDate, plant.wateringAlarm.time)

            //check if the nextDateTime is in past
            val currentTime = LocalDateTime.now()
            while (nextDateTime < currentTime) {
                nextDateTime = nextDateTime.plusDays(plant.waterPeriod.toLong())
            }

            nextDateTime
        }

    fun resetWateringAlarm() {
        CoroutineScope(ioDispatcher).launch {
            plantRepository.getPlantIdWithAlarmList().forEach { (plantId, onOff) ->
                if (onOff) {
                    setWateringAlarm(plantId, onOff)
                }
            }
        }
    }
}