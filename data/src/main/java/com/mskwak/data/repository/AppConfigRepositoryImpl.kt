package com.mskwak.data.repository

import com.mskwak.data.source.remote.AppConfigService
import com.mskwak.domain.repository.AppConfigRepository
import com.orhanobut.logger.Logger
import java.io.IOException
import javax.inject.Inject

class AppConfigRepositoryImpl @Inject constructor(
    private val appConfigService: AppConfigService
) : AppConfigRepository {

    override suspend fun getLatestAppVersion(): Result<Int> {
        return try {
            val response = appConfigService.getLatestAppVersion()
            val result = if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Logger.e(response.message())
                Result.failure(IOException(response.message()))
            }
            result
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getUpdateContent(versionCode: Int): Result<String> {
        return try {
            val response = appConfigService.getUpdateContent(versionCode)
            val result = if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body == "null") {
                    Result.failure(Exception("content is null"))
                } else {
                    Result.success(body)
                }
            } else {
                Logger.e(response.message())
                Result.failure(IOException(response.message()))
            }
            result
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}