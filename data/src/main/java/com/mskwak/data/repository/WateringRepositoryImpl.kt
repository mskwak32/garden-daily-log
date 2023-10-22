package com.mskwak.data.repository

import com.mskwak.data.source.local.PlantDao
import com.mskwak.domain.repository.WateringRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class WateringRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
) : WateringRepository {

    override suspend fun updateLastWateringDate(date: LocalDate, plantId: Int) =
        withContext(Dispatchers.IO) {
            plantDao.updateLastWatering(date, plantId)
        }

    override suspend fun updateWateringAlarmOnOff(isActive: Boolean, plantId: Int) =
        withContext(Dispatchers.IO) {
            plantDao.updateWateringAlarmOnOff(isActive, plantId)
        }
}