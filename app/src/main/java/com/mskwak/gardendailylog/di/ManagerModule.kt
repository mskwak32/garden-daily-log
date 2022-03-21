package com.mskwak.gardendailylog.di

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.presentation.MainActivity
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

    @Provides
    fun providesNotificationPendingIntent(application: Application): PendingIntent {
        val contentIntent = Intent(application, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return PendingIntent.getActivity(
            application,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}