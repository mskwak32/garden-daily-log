package com.mskwak.gardendailylog.di

import com.mskwak.data.repository.PlantRepositoryImpl
import com.mskwak.data.repository.RecordRepositoryImpl
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.repository.RecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsPlantRepository(plantRepository: PlantRepositoryImpl): PlantRepository

    @Singleton
    @Binds
    abstract fun bindsRecordRepository(recordRepository: RecordRepositoryImpl): RecordRepository
}