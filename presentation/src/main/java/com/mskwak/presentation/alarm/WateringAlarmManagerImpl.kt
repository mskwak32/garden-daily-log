package com.mskwak.presentation.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mskwak.domain.manager.WateringAlarmManager
import com.mskwak.domain.model.Plant
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.*

class WateringAlarmManagerImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : WateringAlarmManager {
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun setWateringAlarm(plant: Plant) {
        val intent = Intent(context, WateringAlarmReceiver::class.java)
        intent.putExtra(WateringAlarmReceiver.PLANT_NAME_KEY, plant.name)
        intent.putExtra(WateringAlarmReceiver.PLANT_ID_KEY, plant.id)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plant.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        //TODO 테스트용 코드 제거
//        val triggerTime = getTriggerTime(plant)
//        val interval = TimeUnit.DAYS.toMillis(plant.waterPeriod.toLong())
        val triggerTime = System.currentTimeMillis() + 10000
        val interval = 60000L

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            interval,
            pendingIntent
        )

        Logger.d(
            """set watering alarm: plantId= ${plant.id} 
                trigger= ${Date(triggerTime)}, interval = ${interval / 60000}min"""
        )
    }

    /**
     * 다음 알람이 울릴 시간.
     */
    private suspend fun getTriggerTime(plant: Plant): Long = withContext(dispatcher) {
        val nextDate = plant.lastWateringDate.plusDays(plant.waterPeriod.toLong())
        var nextDateTime = LocalDateTime.of(nextDate, plant.wateringAlarm.time)

        //check if the nextDateTime is in past
        val currentTime = LocalDateTime.now()
        while (nextDateTime < currentTime) {
            nextDateTime = nextDateTime.plusDays(plant.waterPeriod.toLong())
        }


        val calender = Calendar.getInstance().apply {
            set(
                nextDateTime.year,
                nextDateTime.monthValue - 1,
                nextDateTime.dayOfMonth,
                nextDateTime.hour,
                nextDateTime.minute,
                0
            )
        }
        calender.timeInMillis
    }

    override fun cancelWateringAlarm(plant: Plant) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plant.id,
            Intent(context, WateringAlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        Logger.d("cancel watering alarm: plantId= ${plant.id}")
    }
}