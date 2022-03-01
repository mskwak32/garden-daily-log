package com.mskwak.data

import com.mskwak.data.model.AlarmData
import com.mskwak.data.model.PlantData
import com.mskwak.data.model.RecordData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object MockPlantUtil {

    fun mockPlant() = PlantData(
        name = "plant",
        createdDate = LocalDate.now(),
        waterPeriod = 1,
        lastWateringDate = LocalDate.now(),
        wateringAlarm = AlarmData(LocalTime.now(), false),
        pictureUri = null,
        memo = null
    )

    fun mockRecord(plantId: Int) = RecordData(
        plantId = plantId,
        memo = "memo",
        pictureList = listOf(),
        createdTime = LocalDateTime.now()
    )
}