package com.treaty.dailytask

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.treaty.dailytask.model.Reminder
import com.treaty.dailytask.repository.reminder.ReminderRepository
import com.treaty.dailytask.utility.AlarmUtility
import com.treaty.dailytask.utility.NetworkUtility
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {
    private val networkUtility: NetworkUtility by inject()
    private val alarmUtility: AlarmUtility by inject()
    private val reminderRepository: ReminderRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        Log.d("NETWORK", "onCreate: ${networkUtility.isOnline()}")
        lifecycleScope.launch {
            val repo = reminderRepository.getReminderStatus()
            if(repo.isNotEmpty() && !repo[0].isCompleted) {
                alarmUtility.startAlarm()
                reminderRepository.updateReminderTrigger(Reminder(true))
            } else {
                reminderRepository.updateReminderTrigger(Reminder(false))
            }
        }
    }
}