package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import com.treaty.dailytask.utility.AlarmUtility
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import java.time.LocalDateTime

class ReminderDAO(private val realm: Realm, private val alarmUtility: AlarmUtility) :
    ReminderRepository {
    override suspend fun updateReminderTrigger(reminder: Reminder, currentTime: LocalDateTime) {
        realm.write {
            copyToRealm(
                reminder,
                UpdatePolicy.ALL
            )
        }
        alarmUtility.setAlarm(currentTime)
        alarmUtility.startAlarm()
    }

    override suspend fun getReminderStatus(): Result<Reminder?> {
        val realmResult = realm.query(Reminder::class).find()
        val reminderResult = realmResult.getOrNull(0)
        if (reminderResult != null) {
            return Result.success(realmResult[0])
        }
        return Result.failure(Throwable("Error Message"))
    }

    override fun stopReminder(): Result<String> {
        return alarmUtility.stopAlarm()
    }

}
