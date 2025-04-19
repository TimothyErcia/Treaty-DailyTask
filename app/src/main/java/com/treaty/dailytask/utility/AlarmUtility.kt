package com.treaty.dailytask.utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.treaty.dailytask.receivers.AlarmReceivers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class AlarmUtility(private val context: Context) {
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private lateinit var currentTime: LocalDateTime
    private lateinit var intent: Intent
    private lateinit var pendingIntent: PendingIntent

    fun setAlarm(time: LocalDateTime) {
        stopAlarm()
        currentTime = time
    }

    fun startAlarm() {
        intent = Intent(context, AlarmReceivers::class.java)
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val offset = ZonedDateTime.of(currentTime.toLocalDate(), currentTime.toLocalTime(), ZoneId.of("Asia/Manila")).offset
        alarmManager.setRepeating(
            AlarmManager.RTC,
            currentTime.toEpochSecond(offset),
            AlarmManager.INTERVAL_DAY,
            pendingIntent)
    }

    fun stopAlarm() {
        try {
            alarmManager.cancel(pendingIntent)
        } catch (_: Exception) {}
    }
}
