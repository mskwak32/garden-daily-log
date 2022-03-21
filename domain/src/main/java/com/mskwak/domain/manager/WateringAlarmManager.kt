package com.mskwak.domain.manager

import com.mskwak.domain.model.Alarm
import com.mskwak.domain.model.Plant

interface WateringAlarmManager {
    fun setWateringAlarm(plant: Plant)
    fun cancelWateringAlarm(alarm: Alarm)
}