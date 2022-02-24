package com.mskwak.data

import com.mskwak.data.model.AlarmData
import com.mskwak.data.model.PlantData
import com.mskwak.data.model.RecordData
import java.util.*

object MockPlantUtil {

    fun mockPlant() = PlantData(
        name = "plant",
        createdDate = Date(System.currentTimeMillis()),
        waterPeriod = 1,
        lastWateringDate = Date(System.currentTimeMillis()),
        wateringAlarm = AlarmData(9, 0, false),
        pictureUri = null,
        memo = null
    )

    fun mockRecord(plantId: Int) = RecordData(
        plantId = plantId,
        memo = "memo",
        pictureList = listOf(),
        createdTime = Date()
    )
}