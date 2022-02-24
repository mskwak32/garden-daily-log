package com.mskwak.data.model

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mskwak.domain.model.Plant
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "plant")
data class PlantImpl(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val name: String,
    override val createdDate: LocalDateTime,
    override val waterPeriod: Int,
    override val lastWateringDate: LocalDate,
    @Embedded override val wateringAlarm: AlarmImpl,
    override val pictureUri: Uri?,
    override val memo: String?
) : Plant