package com.mskwak.data.source.local

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mskwak.data.model.DiaryData
import com.mskwak.data.model.PlantData

@Database(
    entities = [PlantData::class, DiaryData::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = GardenDatabase.Migration_1_2::class)
    ]
)
@TypeConverters(DatabaseConverter::class)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun diaryDao(): DiaryDao

    @DeleteColumn(tableName = "plant", columnName = "alarmCode")
    class Migration_1_2 : AutoMigrationSpec

    companion object {
        const val DB_NAME = "garden.db"
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE INDEX IF NOT EXISTS 'index_plant_id_name' ON plant (id, name)")
            }
        }
    }
}