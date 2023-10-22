package com.mskwak.data.model

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "plant",
    indices = [Index(value = ["id", "name"])]
)
data class PlantData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdDate: LocalDate,
    val waterPeriod: Int,
    val lastWateringDate: LocalDate,
    @Embedded val wateringAlarm: AlarmData,
    val pictureUri: Uri?,
    val memo: String?
)