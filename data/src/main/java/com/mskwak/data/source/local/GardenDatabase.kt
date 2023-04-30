package com.mskwak.data.source.local

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mskwak.data.model.DiaryData
import com.mskwak.data.model.PlantData

@Database(
    entities = [PlantData::class, DiaryData::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(DatabaseConverter::class)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun diaryDao(): DiaryDao

    companion object {
        const val DB_NAME = "garden.db"

        val Migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE plant DROP COLUMN alarmCode")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE INDEX IF NOT EXISTS 'index_plant_id_name' ON plant (id, name)")
            }
        }
    }
}