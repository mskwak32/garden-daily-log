package com.mskwak.data

import com.mskwak.data.model.AlarmData
import com.mskwak.data.model.DiaryData
import com.mskwak.data.model.PlantData
import com.mskwak.domain.model.Alarm
import com.mskwak.domain.model.Diary
import com.mskwak.domain.model.Plant

fun PlantData.toPlant(): Plant {
    return Plant(
        id,
        name,
        createdDate,
        waterPeriod,
        lastWateringDate,
        wateringAlarm.toAlarm(),
        pictureUri,
        memo
    )
}

fun DiaryData.toDiary(): Diary {
    return Diary(
        id,
        plantId,
        memo,
        pictureList,
        createdDate
    )
}

fun AlarmData.toAlarm(): Alarm {
    return Alarm(time, onOff)
}

fun Plant.toPlantData(): PlantData {
    return PlantData(
        id,
        name,
        createdDate,
        waterPeriod,
        lastWateringDate,
        wateringAlarm.toAlarmData(),
        pictureUri,
        memo
    )
}

fun Diary.toDiaryData(): DiaryData {
    return DiaryData(id, plantId, memo, pictureList, createdDate)
}

fun Alarm.toAlarmData(): AlarmData {
    return AlarmData(time, onOff)
}

