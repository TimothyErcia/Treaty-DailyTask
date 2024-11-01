package com.treaty.dailytask.utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.treaty.dailytask.receivers.AlarmReceivers
import java.util.Calendar

class AlarmUtility(private val context: Context) {
    private val alarmManager: AlarmManager?
    private val calendar: Calendar
    private lateinit var intent: Intent
    private lateinit var pendingIntent: PendingIntent

    init {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.AM_PM, Calendar.PM)
        }
    }

    fun startAlarm() {
        intent = Intent(context, AlarmReceivers::class.java)
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun stopAlarm() {
        alarmManager?.cancel(pendingIntent)
    }
}
