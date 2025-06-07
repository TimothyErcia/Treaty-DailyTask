package com.treaty.dailytask.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import com.treaty.dailytask.MainActivity
import com.treaty.dailytask.R

class LocalNotificationUtility(private val context: Context) {

    private lateinit var notificationManager: NotificationManager
    private val titleReminder = "Spending Reminder"
    private val descriptionReminder = "Please add or update spending's for today"
    private val channelID = "channel_reminder_id"
    private val channelName = "channel_reminder"
    private val channelDescription = "channel_reminder_description"
    private val notificationID = 92420

    init {
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID, channelName, importance).apply {
            description = channelDescription
        }
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification() {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setLargeIcon(Icon.createWithResource(context, R.drawable.ic_launcher_foreground))
            setContentTitle(titleReminder)
            setContentText(descriptionReminder)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setContentIntent(pendingIntent)
            setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_DEFAULT)
            setAutoCancel(true)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
    }
}
