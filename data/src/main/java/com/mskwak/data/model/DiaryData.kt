package com.mskwak.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mskwak.domain.model.Diary
import java.time.LocalDate

@Entity(
    tableName = "diary",
    foreignKeys = [ForeignKey(
        entity = PlantData::class,
        parentColumns = ["id"],
        childColumns = ["plantId"],
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["plantId"])]
)
data class DiaryData(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val plantId: Int,
    override val memo: String,
    override val pictureList: List<Uri>?,
    override val createdDate: LocalDate
) : Diary {
    constructor(diary: Diary) : this(
        id = diary.id,
        plantId = diary.plantId,
        memo = diary.memo,
        pictureList = diary.pictureList,
        createdDate = diary.createdDate
    )
}
