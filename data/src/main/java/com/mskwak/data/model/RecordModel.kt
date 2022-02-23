package com.mskwak.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gardeningRecord",
    foreignKeys = [ForeignKey(
        entity = Plant::class,
        parentColumns = ["id"],
        childColumns = ["plantId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["plantId"])]
)
data class RecordModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantId: Int,
    val memo: String,
    val pictureList: List<Uri> = listOf(),
    val createdTime: Long = System.currentTimeMillis()
)
