package com.mskwak.data

import com.mskwak.data.model.AlarmData
import com.mskwak.data.model.DiaryData
import com.mskwak.data.model.PlantData
import java.time.LocalDate
import java.time.LocalTime

object MockPlantUtil {

    fun mockPlant(id: Int = 0) = PlantData(
        name = "plant",
        createdDate = LocalDate.now(),
        waterPeriod = 1,
        lastWateringDate = LocalDate.now(),
        wateringAlarm = AlarmData(LocalTime.now(), false),
        pictureUri = null,
        memo = null,
        id = id
    )

    fun mockDiary(plantId: Int) = DiaryData(
        plantId = plantId,
        memo = "memo",
        pictureList = listOf(),
        createdDate = LocalDate.now()
    )

    fun mockDiary(plantId: Int, createDate: LocalDate) = DiaryData(
        plantId = plantId,
        memo = "memo",
        pictureList = listOf(),
        createdDate = createDate
    )
}