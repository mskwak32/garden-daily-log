package com.mskwak.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mskwak.data.model.DiaryData
import com.mskwak.data.model.PlantData

@Database(
    entities = [PlantData::class, DiaryData::class],
    version = 1
)
@TypeConverters(DatabaseConverter::class)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun diaryDao(): DiaryDao

    companion object {
        const val DB_NAME = "garden.db"
    }
}