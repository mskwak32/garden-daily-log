package com.mskwak.domain.usecase

import com.mskwak.domain.repository.AppConfigRepository

class AppConfigUseCase(private val appConfigRepository: AppConfigRepository) {

    suspend fun getLatestVersion(): Int? {
        val result = appConfigRepository.getLatestAppVersion()
        return if (result.isSuccess) {
            result.getOrNull()
        } else {
            null
        }
    }

    suspend fun getUpdateContent(versionCode: Int): String? {
        val result = appConfigRepository.getUpdateContent(versionCode)

        return if (result.isSuccess) {
            result.getOrNull()?.run {
                replace("\\n", "\n\n").replace("\"","")
            }
        } else {
            null
        }
    }
}