package com.mskwak.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mskwak.data.model.RecordModel
import com.mskwak.data.model.Plant

@Database(
    entities = [Plant::class, RecordModel::class],
    version = 1
)
@TypeConverters(DatabaseConverter::class)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun recordDao(): RecordDao

    companion object {
        const val DB_NAME = "garden.db"
    }
}