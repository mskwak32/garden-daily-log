package com.mskwak.domain.usecase

import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.domain.model.Plant
import com.mskwak.domain.model.WateringDays
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.repository.WateringRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.abs

class WateringUseCase @Inject constructor(
    private val wateringRepository: WateringRepository,
    private val wateringAlarmManager: WateringAlarmManager,
    private val plantRepository: PlantRepository
) {

    suspend fun wateringNow(plant: Plant) {
        val date = LocalDate.now()
        wateringRepository.updateLastWateringDate(date, plant.id)

        //알람이 설정되어있으면 다음 물주기시간으로 알람 재세팅
        if (plant.wateringAlarm.onOff) {
            setWateringAlarm(plant.id, plant.wateringAlarm.onOff)
        }
    }

    suspend fun updateWateringAlarmOnOff(plantId: Int, isActive: Boolean) {
        wateringRepository.updateWateringAlarmOnOff(isActive, plantId)
        setWateringAlarm(plantId, isActive)
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

    private suspend fun getNextAlarmDateTime(plantId: Int): LocalDateTime? {
        val plant = plantRepository.getPlant(plantId)
        if (plant.waterPeriod == 0) return null

        val nextDate = plant.lastWateringDate.plusDays(plant.waterPeriod.toLong())
        var nextDateTime = LocalDateTime.of(nextDate, plant.wateringAlarm.time)

        //check if the nextDateTime is in past
        val currentTime = LocalDateTime.now()
        while (nextDateTime < currentTime) {
            nextDateTime = nextDateTime.plusDays(plant.waterPeriod.toLong())
        }

        return nextDateTime
    }

    suspend fun resetWateringAlarm() {
        plantRepository.getPlantIdWithAlarmList().forEach { (plantId, onOff) ->
            if (onOff) {
                setWateringAlarm(plantId, onOff)
            }
        }
    }

    /**
     * 물주기 주기설정이 없는 경우 마지막 물준날짜로부터 D+00
     * 물주기 주기설정이 있는 경우 다음 물주기까지 D-00
     * 물주기가 지난 경우 D+00
     *
     */
    fun getWateringDays(plant: Plant): WateringDays {
        return if (plant.waterPeriod == 0) {
            WateringDays(getDaysFromLastWatering(plant), true)
        } else {
            val days = abs(getRemainWateringDate(plant))
            WateringDays(days, days < 0)
        }
    }

    /**
     * 물주기 기간이 남은 경우 양수 예) 2일 후 = 2
     * 물주기 기간이 지난 경우 음수 예) 2일 전 = -2
     * 물주기 설정이 없는 경우 10000
     */
    fun getRemainWateringDate(plant: Plant): Int {
        if (plant.waterPeriod == 0) {
            return Int.MAX_VALUE
        }

        val today = LocalDate.now()
        val nextDate = plant.lastWateringDate.plusDays(plant.waterPeriod.toLong())

        return ChronoUnit.DAYS.between(today, nextDate).toInt()
    }

    private fun getDaysFromLastWatering(plant: Plant): Int {
        val today = LocalDate.now()

        if (!today.isAfter(plant.lastWateringDate)) {
            return 0
        }

        return ChronoUnit.DAYS.between(plant.lastWateringDate, today).toInt()
    }
}