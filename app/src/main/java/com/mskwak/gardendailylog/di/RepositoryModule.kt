package com.mskwak.gardendailylog.di

import com.mskwak.data.repository.AppConfigRepositoryImpl
import com.mskwak.data.repository.DiaryRepositoryImpl
import com.mskwak.data.repository.PlantRepositoryImpl
import com.mskwak.domain.repository.AppConfigRepository
import com.mskwak.domain.repository.DiaryRepository
import com.mskwak.domain.repository.PlantRepository
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
    abstract fun bindPlantRepository(repository: PlantRepositoryImpl): PlantRepository

    @Singleton
    @Binds
    abstract fun bindDiaryRepository(repository: DiaryRepositoryImpl): DiaryRepository

    @Singleton
    @Binds
    abstract fun bindAppConfigRepository(repository: AppConfigRepositoryImpl): AppConfigRepository
}