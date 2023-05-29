package com.mskwak.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.type.PlantListSortOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun getPlantsWithSortOrder(sortOrder: PlantListSortOrder): Flow<List<Plant>> {
        return plantRepository.getPlants().map { list ->
            list.applySort(sortOrder)
        }.flowOn(ioDispatcher)
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

    fun getPlantFlow(plantId: Int): Flow<Plant> {
        return plantRepository.getPlantFlow(plantId)
    }

    suspend fun getPlant(plantId: Int): Plant = withContext(ioDispatcher) {
        plantRepository.getPlant(plantId)
    }

    suspend fun addPlant(plant: Plant) {
        withContext(ioDispatcher) {
            val id = plantRepository.addPlant(plant)
            setWateringAlarm(id, isActive = plant.wateringAlarm.onOff)
        }
    }

    suspend fun updatePlant(plant: Plant) {
        withContext(ioDispatcher) {
            plantRepository.updatePlant(plant)
            setWateringAlarm(plant.id, isActive = plant.wateringAlarm.onOff)
        }
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
     * 물주기 주기설정이 없는 경우 마지막 물준날짜로부터 D+00
     * 물주기 주기설정이 있는 경우 다음 물주기까지 D-00
     * 물주기가 지난 경우 D+00
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
     * 물주기 기간이 남은 경우 양수 예) 2일 후 = 2
     * 물주기 기간이 지난 경우 음수 예) 2일 전 = -2
     * 물주기 설정이 없는 경우 10000
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

    suspend fun wateringNow(plant: Plant) {
        withContext(ioDispatcher) {
            val date = LocalDate.now()
            plantRepository.updateLastWateringDate(date, plant.id)

            //알람이 설정되어있으면 다음 물주기시간으로 알람 재세팅
            if (plant.wateringAlarm.onOff) {
                setWateringAlarm(plant.id, plant.wateringAlarm.onOff)
            }
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
            //다음 알람이 없는 경우는 등록하지 않음
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