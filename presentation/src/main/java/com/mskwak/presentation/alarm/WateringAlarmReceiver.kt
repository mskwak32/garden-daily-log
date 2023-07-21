package com.mskwak.presentation.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mskwak.domain.usecase.PlantUseCase
import com.mskwak.domain.usecase.WateringUseCase
import com.mskwak.presentation.MainActivity
import com.mskwak.presentation.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WateringAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var plantUseCase: PlantUseCase

    @Inject
    lateinit var wateringUseCase: WateringUseCase

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)

        val plantId = intent.getIntExtra(PLANT_ID_KEY, DEFAULT_PLANT_ID)

        CoroutineScope(Dispatchers.Default).launch {
            plantUseCase.takeIf { plantId != DEFAULT_PLANT_ID }?.getPlant(plantId)?.first()
                ?.let { plant ->
                    deliverNotification(context, plant.name, plantId)
                    //다음번 알람 등록
                    wateringUseCase.setWateringAlarm(plant.id, true)
                }
        }
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

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_leaf)
                .setContentTitle(context.getString(R.string.noti_watering_title))
                .setContentText(
                    plantName?.let {
                        context.getString(
                            R.string.noti_watering_message,
                            it
                        )
                    } ?: ""
                )
                .setContentIntent(getPendingIntent(context))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(WATERING_GROUP_KEY)

            with(NotificationManagerCompat.from(context)) {
                notify(plantId, builder.build())
                notify(SUMMARY_NOTIFICATION_CODE, getSummaryNotification(context).build())
            }
        }
    }

    private fun getSummaryNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_leaf)
            .setAutoCancel(true)
            .setGroup(WATERING_GROUP_KEY)
            .setOnlyAlertOnce(true)
            .setGroupSummary(true)
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val CHANNEL_ID = "wateringNotification"
        const val PLANT_ID_KEY = "plantId"
        private const val DEFAULT_PLANT_ID = 100000
        private const val SUMMARY_NOTIFICATION_CODE = 1000
        private const val WATERING_GROUP_KEY = "wateringNotificationGroup"
    }
}