package com.mskwak.gardendailylog

import com.mskwak.gardendailylog.data.AlarmModel
import com.mskwak.gardendailylog.data.GardeningRecord
import com.mskwak.gardendailylog.data.Plant
import java.util.*

object MockPlantUtil {

    fun mockPlant() = Plant(
        name = "plant",
        createdDate = Date(System.currentTimeMillis()),
        waterPeriod = 1,
        lastWateringDate = Date(System.currentTimeMillis()),
        alarm = AlarmModel(9, 0, false)
    )

    fun mockRecord(plantId: Int) = GardeningRecord(
        plantId = plantId,
        memo = "memo"
    )
}