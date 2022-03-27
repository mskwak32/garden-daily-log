package com.mskwak.gardendailylog.di

import android.app.Application
import androidx.room.Room
import com.mskwak.data.source.local.DiaryDao
import com.mskwak.data.source.local.FileDataSource
import com.mskwak.data.source.local.GardenDatabase
import com.mskwak.data.source.local.PlantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideGardenDatabase(application: Application): GardenDatabase {
        return Room.databaseBuilder(application, GardenDatabase::class.java, GardenDatabase.DB_NAME)
            .addMigrations(GardenDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun providePlantDao(db: GardenDatabase): PlantDao {
        return db.plantDao()
    }

    @Provides
    @Singleton
    fun provideRecordDao(db: GardenDatabase): DiaryDao {
        return db.diaryDao()
    }

    @Provides
    @Singleton
    fun provideFileDataSource(application: Application): FileDataSource {
        val baseDir = application.filesDir
        return FileDataSource(baseDir)
    }
}