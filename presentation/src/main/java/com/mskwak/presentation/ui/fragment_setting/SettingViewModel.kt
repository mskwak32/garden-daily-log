package com.mskwak.presentation.ui.fragment_setting

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mskwak.domain.usecase.AppConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appConfigUseCase: AppConfigUseCase
) : ViewModel() {

    private var versionCode: Int? = null
    private var versionName: String? = null

    private val _hasAppUpdate = MutableLiveData(false)
    val hasAppUpdate: LiveData<Boolean> = _hasAppUpdate

    var updateContent: String? = null
        private set

    private val lock = ReentrantLock()

    fun getAppVersionName(context: Context): String {
        return if (versionName != null) {
            return versionName!!
        } else {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = info.versionName
            versionName!!
        }
    }

    fun loadLatestAppVersion(context: Context) {
        viewModelScope.launch {
            val latestVersion = appConfigUseCase.getLatestVersion()
            val currentVersion = getVersionCode(context)

            _hasAppUpdate.value = if (latestVersion == null) {
                false
            } else {
                latestVersion > currentVersion
            }
        }
    }

    fun loadUpdateContent(context: Context) {
        viewModelScope.launch {
            updateContent = appConfigUseCase.getUpdateContent(getVersionCode(context))
        }
    }

    private fun getVersionCode(context: Context): Int {
        lock.lock()
        val code = if (versionCode != null) {
            versionCode!!
        } else {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            versionCode = PackageInfoCompat.getLongVersionCode(info).toInt()
            versionCode!!
        }
        lock.unlock()
        return code
    }
}