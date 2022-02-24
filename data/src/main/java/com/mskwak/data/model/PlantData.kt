package com.mskwak.data.model

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mskwak.domain.model.PlantModel
import java.util.*

@Entity(tableName = "plant")
data class PlantData(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val name: String,
    override val createdDate: Date,
    override val waterPeriod: Int,
    override val lastWateringDate: Date,
    @Embedded
    override val wateringAlarm: AlarmData,
    override val pictureUri: Uri?,
    override val memo: String?
) : PlantModel