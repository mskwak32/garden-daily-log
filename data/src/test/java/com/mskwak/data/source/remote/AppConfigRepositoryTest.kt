package com.mskwak.data.source.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mskwak.data.repository.AppConfigRepositoryImpl
import com.mskwak.domain.repository.AppConfigRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.locks.ReentrantLock

class AppConfigRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: AppConfigRepository

    @Before
    fun init() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://garden-daily-log-default-rtdb.firebaseio.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        val appConfigService = retrofit.create(AppConfigService::class.java)
        repository = AppConfigRepositoryImpl(appConfigService)
    }

    @Test
    fun getLatestVersionTest() = runBlocking {
        val result = repository.getLatestAppVersion()
        assert(result.isSuccess)
        assert(result.getOrNull() != null)
    }

    @Test
    fun getUpdateContentTest() = runBlocking {
        val result = repository.getUpdateContent(2)
        assert(result.isSuccess)
        val content = result.getOrNull()
        assert(content?.isNotBlank() == true)
    }
}