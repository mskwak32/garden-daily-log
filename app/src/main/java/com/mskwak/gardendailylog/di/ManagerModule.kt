package com.mskwak.gardendailylog.di

import android.app.Application
import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.presentation.alarm.WateringAlarmManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ManagerModule {

    @Provides
    @Singleton
    fun providesAlarmManager(application: Application): WateringAlarmManager {
        return WateringAlarmManagerImpl(application)
    }
}