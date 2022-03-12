package com.mskwak.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.mskwak.domain.model.Diary
import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.repository.PlantRepository
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

class GardenUseCase(
    private val plantRepository: PlantRepository,
    private val diaryRepository: DiaryRepository,
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

    fun observePlant(plantId: Int): LiveData<Plant> {
        return plantRepository.observePlant(plantId)
    }

    suspend fun getPlant(plantId: Int): Plant = withContext(ioDispatcher) {
        plantRepository.getPlant(plantId)
    }

    suspend fun addPlant(plant: Plant) = withContext(ioDispatcher) {
        plantRepository.addPlant(plant)
    }

    suspend fun updatePlant(plant: Plant) = withContext(ioDispatcher) {
        plantRepository.updatePlant(plant)
    }

    fun deletePlant(plant: Plant) {
        CoroutineScope(ioDispatcher).launch {
            diaryRepository.deleteDiariesByPlantId(plant.id)
            plantRepository.deletePlant(plant)
        }
    }

    suspend fun getPlantName(plantId: Int) = withContext(ioDispatcher) {
        plantRepository.getPlantName(plantId)
    }

    fun getDiaries(): LiveData<List<Diary>> {
        return diaryRepository.observeDiaries()
    }

    fun observeDiariesByPlantId(plantId: Int): LiveData<List<Diary>> {
        return diaryRepository.observeDiariesByPlantId(plantId)
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

    suspend fun getDiaryById(diaryId: Int): Diary {
        return diaryRepository.getDiaryById(diaryId)
    }

    fun observeDiaryById(diaryId: Int): LiveData<Diary> {
        return diaryRepository.observeDiaryById(diaryId)
    }

    /**
     * 물주기 주기설정이 없는 경우 마지막 물준날짜로부터 D+00
     * 물주기 주기설정이 있는 경우 다음 물주기까지 D-00
     */
    fun getDdayText(plant: Plant): String {
        return if (plant.waterPeriod == 0) {
            String.format("D+%02d", getDaysFromLastWatering(plant))
        } else {
            String.format("D-%02d", getRemainWateringDate(plant))
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

    fun deletePicture(vararg uri: Uri) {
        CoroutineScope(ioDispatcher).launch {
            uri.forEach { plantRepository.deletePlantPicture(it) }
        }
    }

    suspend fun wateringNow(plantId: Int) = withContext(ioDispatcher) {
        val date = LocalDate.now()
        plantRepository.updateLastWateringDate(date, plantId)
    }

    fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int) {
        CoroutineScope(ioDispatcher).launch {
            plantRepository.updateWateringAlarmOnOff(isActive, plantId)
        }
    }

}