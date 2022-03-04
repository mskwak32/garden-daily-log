package com.mskwak.gardendailylog.di

import android.app.Application
import androidx.room.Room
import com.mskwak.data.source.FileDataSource
import com.mskwak.data.source.GardenDatabase
import com.mskwak.data.source.PlantDao
import com.mskwak.data.source.RecordDao
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
    fun providesGardenDatabase(application: Application): GardenDatabase {
        return Room.databaseBuilder(application, GardenDatabase::class.java, GardenDatabase.DB_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun providesPlantDao(db: GardenDatabase): PlantDao {
        return db.plantDao()
    }

    @Provides
    @Singleton
    fun providesRecordDao(db: GardenDatabase): RecordDao {
        return db.recordDao()
    }

    @Provides
    @Singleton
    fun provideFileDataSource(application: Application): FileDataSource {
        val baseDir = application.filesDir
        return FileDataSource(baseDir)
    }
}