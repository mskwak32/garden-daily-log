package com.mskwak.presentation.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import com.mskwak.presentation.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WateringAlarmReceiver : BroadcastReceiver() {

    //TODO 알림 그룹
    @Inject
    lateinit var notificationPendingIntent: PendingIntent

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)

        val plantName = intent.getStringExtra(PLANT_NAME_KEY)
        val alarmCode = intent.getIntExtra(ALARM_CODE_KEY, DEFAULT_ALARM_CODE)
        deliverNotification(context, plantName, alarmCode)
    }

    private fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel =
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.noti_watering_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun deliverNotification(context: Context, plantName: String?, alarmCode: Int) {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_leaf)
            .setContentTitle(context.getString(R.string.noti_watering_title))
            .setContentText(plantName?.let { context.getString(R.string.noti_watering_message, it) }
                ?: "")
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setVisibility(VISIBILITY_PUBLIC)
            .setGroup(WATERING_GROUP)

        with(NotificationManagerCompat.from(context)) {
            notify(alarmCode, builder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "wateringNotification"
        const val PLANT_NAME_KEY = "plantName"
        const val ALARM_CODE_KEY = "alarmCode"
        private const val DEFAULT_ALARM_CODE = 1
        private const val WATERING_GROUP = "com.mskwak.gardendailylog.watering"
    }
}