package com.mskwak.data.model

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mskwak.domain.model.AlarmModel
import java.util.*

@Entity(tableName = "plant")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdDate: Date,
    val waterPeriod: Int,
    val lastWateringDate: Date,
    @Embedded val alarm: AlarmModelImpl,
    val pictureUri: Uri? = null,
    val memo: String? = null
)