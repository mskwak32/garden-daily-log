package com.mskwak.presentation.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mskwak.presentation.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WateringAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationPendingIntent: PendingIntent

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)

        val plantName = intent.getStringExtra(PLANT_NAME_KEY)
        val plantId = intent.getIntExtra(PLANT_ID_KEY, DEFAULT_PLANT_ID)
        deliverNotification(context, plantName, plantId)
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

    private fun deliverNotification(context: Context, plantName: String?, plantId: Int) {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_leaf)
            .setContentTitle(context.getString(R.string.noti_watering_title))
            .setContentText(plantName?.let { context.getString(R.string.noti_watering_message, it) }
                ?: "")
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setGroup(WATERING_GROUP_KEY)

        with(NotificationManagerCompat.from(context)) {
            notify(plantId, builder.build())
            notify(SUMMARY_NOTI_CODE, getSummaryNoti(context).build())
        }
    }

    private fun getSummaryNoti(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_leaf)
            .setAutoCancel(true)
            .setGroup(WATERING_GROUP_KEY)
            .setOnlyAlertOnce(true)
            .setGroupSummary(true)
    }

    companion object {
        private const val CHANNEL_ID = "wateringNotification"
        const val PLANT_NAME_KEY = "plantName"
        const val PLANT_ID_KEY = "plantId"
        private const val DEFAULT_PLANT_ID = 100000
        private const val SUMMARY_NOTI_CODE = 1000
        private const val WATERING_GROUP_KEY = "wateringNotificationGroup"
    }
}