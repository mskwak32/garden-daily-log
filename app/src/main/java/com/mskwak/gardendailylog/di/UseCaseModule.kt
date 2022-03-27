package com.mskwak.gardendailylog.di

import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.domain.repository.AppConfigRepository
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.usecase.AppConfigUseCase
import com.mskwak.domain.usecase.DiaryUseCase
import com.mskwak.domain.usecase.PlantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun providePlantUseCase(
        plantRepository: PlantRepository,
        diaryRepository: DiaryRepository,
        wateringAlarmManager: WateringAlarmManager
    ): PlantUseCase {
        return PlantUseCase(plantRepository, diaryRepository, wateringAlarmManager)
    }

    @Singleton
    @Provides
    fun provideDiaryUseCase(diaryRepository: DiaryRepository): DiaryUseCase {
        return DiaryUseCase(diaryRepository)
    }

    @Singleton
    @Provides
    fun provideAppConfigUseCase(appConfigRepository: AppConfigRepository): AppConfigUseCase {
        return AppConfigUseCase(appConfigRepository)
    }
}