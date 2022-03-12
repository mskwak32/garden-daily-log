package com.mskwak.gardendailylog.di

import com.mskwak.data.repository.DiaryRepositoryImpl
import com.mskwak.data.repository.PlantRepositoryImpl
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
    abstract fun bindsPlantRepository(plantRepository: PlantRepositoryImpl): PlantRepository

    @Singleton
    @Binds
    abstract fun bindsDiaryRepository(diaryRepository: DiaryRepositoryImpl): DiaryRepository
}