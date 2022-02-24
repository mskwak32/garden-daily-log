package com.mskwak.data

import com.mskwak.data.model.AlarmImpl
import com.mskwak.data.model.PlantImpl
import com.mskwak.data.model.RecordImpl
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object MockPlantUtil {

    fun mockPlant() = PlantImpl(
        name = "plant",
        createdDate = LocalDateTime.now(),
        waterPeriod = 1,
        lastWateringDate = LocalDate.now(),
        wateringAlarm = AlarmImpl(LocalTime.now(), false),
        pictureUri = null,
        memo = null
    )

    fun mockRecord(plantId: Int) = RecordImpl(
        plantId = plantId,
        memo = "memo",
        pictureList = listOf(),
        createdTime = LocalDateTime.now()
    )
}