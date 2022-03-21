package com.mskwak.presentation.ui.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {
    private var appVersionName: String? = null

    fun getAppVersionName(context: Context): String {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        appVersionName = info.versionName
        return appVersionName!!
    }
}