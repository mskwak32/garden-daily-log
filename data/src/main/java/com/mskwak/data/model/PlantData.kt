package com.mskwak.data.model

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mskwak.domain.model.Plant
import java.time.LocalDate

@Entity(
    tableName = "plant",
    indices = [Index(value = ["id", "name"])]
)
data class PlantData(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val name: String,
    override val createdDate: LocalDate,
    override val waterPeriod: Int,
    override val lastWateringDate: LocalDate,
    @Embedded override val wateringAlarm: AlarmData,
    override val pictureUri: Uri?,
    override val memo: String?
) : Plant {

    constructor(plant: Plant) : this(
        id = plant.id,
        name = plant.name,
        createdDate = plant.createdDate,
        waterPeriod = plant.waterPeriod,
        lastWateringDate = plant.lastWateringDate,
        wateringAlarm = AlarmData(plant.wateringAlarm),
        pictureUri = plant.pictureUri,
        memo = plant.memo
    )
}