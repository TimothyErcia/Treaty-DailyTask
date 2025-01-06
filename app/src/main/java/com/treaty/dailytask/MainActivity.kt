package com.treaty.dailytask

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.treaty.dailytask.model.Reminder
import com.treaty.dailytask.repository.reminder.ReminderRepositoryImpl
import com.treaty.dailytask.utility.AlarmUtility
import com.treaty.dailytask.utility.NetworkUtility
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.time.ZoneOffset


class MainActivity : AppCompatActivity() {
    private val networkUtility: NetworkUtility by inject()
    private val alarmUtility: AlarmUtility by inject()
    private val reminderRepositoryImpl: ReminderRepositoryImpl by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        Log.d("NETWORK", "onCreate: ${networkUtility.isOnline()}")
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            checkAlarm()
        }
    }
    private suspend fun checkAlarm() {
        val repo = reminderRepositoryImpl.getReminderStatus()
        Log.d("TAG", "checkAlarm: CURRENT ${LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)}")
        Log.d("TAG", "checkAlarm: TRIGGER ${repo?.dateOfTrigger?.toLong()}")
        if(repo != null) {
            val triggerDate =
                repo.dateOfTrigger.toLong()
            val currentDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

            if(currentDate < triggerDate) {
                alarmUtility.stopAlarm()
                reminderRepositoryImpl.updateReminderTrigger(Reminder(true))
            } else if(repo.isCompleted) {
                startAlarm()
            }
        } else {
            startAlarm()
        }
    }

    private suspend fun startAlarm() {
        alarmUtility.startAlarm()
        reminderRepositoryImpl.updateReminderTrigger(Reminder(false))
    }
}