package com.mskwak.gardendailylog.di

import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.usecase.GardenUseCase
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
    fun provideGardenUseCase(
        plantRepository: PlantRepository,
        diaryRepository: DiaryRepository,
        wateringAlarmManager: WateringAlarmManager
    ): GardenUseCase {
        return GardenUseCase(plantRepository, diaryRepository, wateringAlarmManager)
    }
}