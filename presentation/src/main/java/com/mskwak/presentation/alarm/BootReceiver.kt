package com.mskwak.presentation.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mskwak.domain.usecase.PlantUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var useCase: PlantUseCase
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            resetWateringAlarm()
        }
    }

    /**
     * 부팅후 알람 재등록
     */
    private fun resetWateringAlarm() {
        CoroutineScope(dispatcher).launch {
            useCase.getPlantAlarmList().forEach { (plantId, onOff) ->
                if (onOff) {
                    useCase.setWateringAlarm(plantId, onOff)
                }
            }
        }
    }
}