package com.mskwak.data

import com.mskwak.data.model.AlarmModelImpl
import com.mskwak.data.model.RecordModel
import com.mskwak.data.model.Plant
import java.util.*

object MockPlantUtil {

    fun mockPlant() = Plant(
        name = "plant",
        createdDate = Date(System.currentTimeMillis()),
        waterPeriod = 1,
        lastWateringDate = Date(System.currentTimeMillis()),
        alarm = AlarmModelImpl(9, 0, false)
    )

    fun mockRecord(plantId: Int) = RecordModel(
        plantId = plantId,
        memo = "memo"
    )
}