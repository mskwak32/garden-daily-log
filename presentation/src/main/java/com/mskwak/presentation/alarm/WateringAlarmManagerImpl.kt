package com.mskwak.presentation.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mskwak.domain.manager.WateringAlarmManager
import timber.log.Timber
import java.time.LocalDateTime
import java.util.*

class WateringAlarmManagerImpl(private val context: Context) : WateringAlarmManager {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun setWateringAlarm(plantId: Int, nextAlarmDateTime: LocalDateTime) {
        val intent = Intent(context, WateringAlarmReceiver::class.java)
        intent.putExtra(WateringAlarmReceiver.PLANT_ID_KEY, plantId)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plantId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                nextAlarmDateTime.getTimeMillis(),
                pendingIntent
            ), pendingIntent
        )

        Timber.d("set watering alarm: plantId= $plantId trigger= $nextAlarmDateTime")
    }

    override fun cancelWateringAlarm(plantId: Int) {
        //FLAG_NO_CREATE로 기존 생성된 pendingIntent 반환
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plantId,
            Intent(context, WateringAlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            Timber.d("cancel watering alarm: plantId= $plantId")
        }
    }

    private fun LocalDateTime.getTimeMillis(): Long {
        val calender = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(
                year,
                monthValue.minus(1),
                dayOfMonth,
                hour,
                minute,
                0
            )
        }
        return calender.timeInMillis
    }
}