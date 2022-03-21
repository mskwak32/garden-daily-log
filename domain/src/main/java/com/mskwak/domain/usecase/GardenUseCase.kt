package com.mskwak.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.mskwak.domain.AppConstValue
import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.domain.model.Diary
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.repository.PlantRepository
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class GardenUseCase(
    private val plantRepository: PlantRepository,
    private val diaryRepository: DiaryRepository,
    private val wateringAlarmManager: WateringAlarmManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    fun getPlantsWithSortOrder(sortOrder: PlantListSortOrder): LiveData<List<Plant>> {
        val plantsLiveData = plantRepository.getPlants()
        return plantsLiveData.switchMap { list ->
            liveData {
                emit(list.applySort(sortOrder))
            }
        }
    }

    private suspend fun List<Plant>.applySort(sortOrder: PlantListSortOrder): List<Plant> =
        withContext(defaultDispatcher) {
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

    fun getPlantLiveData(plantId: Int): LiveData<Plant> {
        return plantRepository.getPlantLiveData(plantId)
    }

    suspend fun getPlant(plantId: Int): Plant = withContext(ioDispatcher) {
        plantRepository.getPlant(plantId)
    }

    suspend fun addPlant(plant: Plant) = withContext(ioDispatcher) {
        plantRepository.addPlant(plant)
        setWateringAlarm(plant, isActive = plant.wateringAlarm.onOff)
    }

    suspend fun updatePlant(plant: Plant) = withContext(ioDispatcher) {
        plantRepository.updatePlant(plant)
        setWateringAlarm(plant, isActive = plant.wateringAlarm.onOff)
    }

    fun deletePlant(plant: Plant) {
        CoroutineScope(ioDispatcher).launch {
            diaryRepository.deleteDiariesByPlantId(plant.id)
            plantRepository.deletePlant(plant)
            setWateringAlarm(plant, isActive = false)
        }
    }

    suspend fun getPlantName(plantId: Int) = withContext(ioDispatcher) {
        plantRepository.getPlantName(plantId)
    }

    fun getDiariesByPlantId(plantId: Int): LiveData<List<Diary>> {
        return diaryRepository.getDiariesByPlantId(
            plantId,
            AppConstValue.MAX_DIARY_SIZE_ON_PLANT_DETAIL
        )
    }

    suspend fun addDiary(diary: Diary) = withContext(ioDispatcher) {
        diaryRepository.addDiary(diary)
    }

    suspend fun updateDiary(diary: Diary) = withContext(ioDispatcher) {
        diaryRepository.updateDiary(diary)
    }

    fun deleteDiary(diary: Diary) {
        CoroutineScope(ioDispatcher).launch {
            diaryRepository.deleteDiary(diary)
        }
    }

    suspend fun getDiary(diaryId: Int): Diary {
        return diaryRepository.getDiary(diaryId)
    }

    fun getDiaryLiveData(diaryId: Int): LiveData<Diary> {
        return diaryRepository.getDiaryLiveData(diaryId)
    }

    suspend fun getPlantNames(): Map<Int, String> {
        return plantRepository.getPlantNames()
    }

    fun getDiaries(
        year: Int,
        month: Int,
        sortOrder: DiaryListSortOrder,
        plantId: Int? = null
    ): LiveData<List<Diary>> {
        val diariesLiveData = diaryRepository.getDiaries(year, month, plantId)
        return diariesLiveData.switchMap { list ->
            liveData {
                emit(list.applySort(sortOrder))
            }
        }
    }

    private suspend fun List<Diary>.applySort(sortOrder: DiaryListSortOrder): List<Diary> =
        withContext(defaultDispatcher) {
            when (sortOrder) {
                DiaryListSortOrder.CREATED_LATEST -> {
                    sortedByDescending { diary -> diary.createdDate }
                }
                DiaryListSortOrder.CREATED_OLDEST -> {
                    sortedBy { diary -> diary.createdDate }
                }
            }
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

    suspend fun wateringNow(plantId: Int) = withContext(ioDispatcher) {
        val date = LocalDate.now()
        plantRepository.updateLastWateringDate(date, plantId)
    }

    fun updateWateringAlarmOnOff(isActive: Boolean, plant: Plant) {
        CoroutineScope(ioDispatcher).launch {
            plantRepository.updateWateringAlarmOnOff(isActive, plant.id)

            setWateringAlarm(plant, isActive)
        }
    }

    private suspend fun setWateringAlarm(plant: Plant, isActive: Boolean) =
        withContext(defaultDispatcher) {
            if (isActive) {
                wateringAlarmManager.setWateringAlarm(plant)
            } else {
                wateringAlarmManager.cancelWateringAlarm(plant.wateringAlarm)
            }
        }
}