package com.treaty.dailytask.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.treaty.dailytask.repository.reminder.ReminderRepositoryImpl
import com.treaty.dailytask.utility.LocalNotificationUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceivers : BroadcastReceiver(), KoinComponent {
    private val reminderRepositoryImpl: ReminderRepositoryImpl by inject()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val localNotificationUtility = LocalNotificationUtility(it)
            localNotificationUtility.showNotification()

            scope.launch {
                val repo = reminderRepositoryImpl.getReminderStatus()
                val computedNextTrigger = repo.dateOfTrigger + 86400
                reminderRepositoryImpl.updateReminderTrigger(
                    true,
                    reminderRepositoryImpl.toLocalDateTime(computedNextTrigger)
                )
            }
        }
    }

}
