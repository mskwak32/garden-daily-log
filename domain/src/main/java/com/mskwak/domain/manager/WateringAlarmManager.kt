package com.mskwak.domain.manager

import java.time.LocalDateTime

interface WateringAlarmManager {
    fun setWateringAlarm(plantId: Int, nextAlarmDateTime: LocalDateTime)
    fun cancelWateringAlarm(plantId: Int)
}