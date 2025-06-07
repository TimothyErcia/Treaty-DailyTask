package com.treaty.dailytask.utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.treaty.dailytask.receivers.AlarmReceivers
import java.time.LocalDateTime
import java.util.Calendar

class AlarmUtility(context: Context) {
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var intent: Intent = Intent(context, AlarmReceivers::class.java)
    private var pendingIntent: PendingIntent =
        PendingIntent.getBroadcast(
            context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    fun startAlarm(time: LocalDateTime): Result<String> {
        val calendar = convertToCalendar(time)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        return Result.success("Alarm Enabled")
    }

    private fun convertToCalendar(time: LocalDateTime): Calendar {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, time.second)
        }
        return calendar
    }

    fun stopAlarm(): Result<String> {
        alarmManager.cancel(pendingIntent)
        return Result.success("Alarm Stopped")
    }
}
