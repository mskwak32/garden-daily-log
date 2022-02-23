package com.mskwak.gardendailylog.di

import android.app.Application
import androidx.room.Room
import com.mskwak.data.source.GardenDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideGardenDatabase(application: Application): GardenDatabase {
        return Room.databaseBuilder(application, GardenDatabase::class.java, GardenDatabase.DB_NAME)
            .build()
    }
}