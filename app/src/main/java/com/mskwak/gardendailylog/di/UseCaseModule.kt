package com.mskwak.gardendailylog.di

import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.repository.RecordRepository
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
        recordRepository: RecordRepository
    ): GardenUseCase {
        return GardenUseCase(plantRepository, recordRepository)
    }
}