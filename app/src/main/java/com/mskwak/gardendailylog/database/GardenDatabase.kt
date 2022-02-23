package com.mskwak.gardendailylog.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mskwak.gardendailylog.data.GardeningRecord
import com.mskwak.gardendailylog.data.Plant

@Database(
    entities = [Plant::class, GardeningRecord::class],
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