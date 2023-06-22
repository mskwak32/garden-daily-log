package com.mskwak.gardendailylog.di

import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.presentation.alarm.WateringAlarmManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun bindAlarmManager(repository: WateringAlarmManagerImpl): WateringAlarmManager
}