package com.mskwak.gardendailylog.data

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "plant")
data class Plant(
    @PrimaryKey(autoGenerate = true) val plantId: Int = 0,
    val name: String,
    val createdDate: Date,
    val waterPeriod: Int,
    val lastWateringDate: Date,
    @Embedded val alarm: AlarmModel,
    val pictureUri: Uri? = null,
    val memo: String? = null
)