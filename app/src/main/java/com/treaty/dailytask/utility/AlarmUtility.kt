package com.treaty.dailytask.utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.treaty.dailytask.receivers.AlarmReceivers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class AlarmUtility(context: Context) {
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private lateinit var currentTime: LocalDateTime
    private var intent: Intent = Intent(context, AlarmReceivers::class.java)
    private var pendingIntent: PendingIntent =
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    fun setAlarm(time: LocalDateTime) {
        stopAlarm()
        currentTime = time
    }

    fun startAlarm(): Result<String> {
        if (alarmManager == null) {
            return Result.failure(Throwable("Error Message"))
        }
        val offset = ZonedDateTime.of(currentTime.toLocalDate(), currentTime.toLocalTime(), ZoneId.of("Asia/Manila")).offset
        alarmManager.setRepeating(
            AlarmManager.RTC,
            currentTime.toEpochSecond(offset),
            AlarmManager.INTERVAL_DAY,
            pendingIntent)
        return Result.success("Alarm Enabled")
    }

    fun stopAlarm(): Result<String> {
        if (pendingIntent == null) {
            return Result.failure(Throwable("Error Message"))
        }
        alarmManager.cancel(pendingIntent)
        return Result.success("Alarm Stopped")
    }
}
