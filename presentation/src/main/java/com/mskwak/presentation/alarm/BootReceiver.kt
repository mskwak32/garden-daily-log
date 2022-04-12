package com.mskwak.presentation.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mskwak.domain.usecase.PlantUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var useCase: PlantUseCase

    /**
     * 부팅후 또는 앱 업데이트후 알람 재등록
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED) ||
            intent.action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)
        ) {
            useCase.resetWateringAlarm()
        }
    }
}