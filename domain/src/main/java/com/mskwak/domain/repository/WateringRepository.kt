package com.mskwak.domain.repository

import java.time.LocalDate

interface WateringRepository {
    suspend fun updateLastWateringDate(date: LocalDate, plantId: Int)
    suspend fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int)
}