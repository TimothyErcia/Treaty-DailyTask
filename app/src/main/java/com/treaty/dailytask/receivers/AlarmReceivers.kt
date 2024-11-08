package com.treaty.dailytask.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.treaty.dailytask.utility.LocalNotificationUtility

class AlarmReceivers: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val localNotificationUtility = LocalNotificationUtility(it)
            localNotificationUtility.showNotification()
        }
    }

}
