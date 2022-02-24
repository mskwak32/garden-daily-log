package com.mskwak.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mskwak.domain.model.RecordModel
import java.util.*

@Entity(
    tableName = "gardeningRecord",
    foreignKeys = [ForeignKey(
        entity = PlantData::class,
        parentColumns = ["id"],
        childColumns = ["plantId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["plantId"])]
)
data class RecordData(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val plantId: Int,
    override val memo: String,
    override val pictureList: List<Uri>,
    override val createdTime: Date
) : RecordModel
