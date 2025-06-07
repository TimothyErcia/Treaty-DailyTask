package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import com.treaty.dailytask.utility.AlarmUtility
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.query.RealmResults
import java.time.LocalDateTime

class ReminderDAO(private val realm: Realm, private val alarmUtility: AlarmUtility) :
    ReminderRepository {
    override suspend fun updateReminderTrigger(
        reminder: Reminder,
        currentTime: LocalDateTime
    ): Result<String> {
        realm.write {
            copyToRealm(
                reminder,
                UpdatePolicy.ALL
            )
        }
        val alarm = alarmUtility.startAlarm(currentTime)
        return alarm
    }

    override suspend fun getReminderStatus(): Result<Reminder?> {
        val realmResult: RealmResults<Reminder> = realm.query(Reminder::class).find()
        if (realmResult.isNotEmpty()) {
            val reminderResult: Reminder = realmResult.first()
            return Result.success(reminderResult)
        }
        return Result.failure(Throwable("Error Message"))
    }

    override fun stopReminder(): Result<String> {
        return alarmUtility.stopAlarm()
    }

}
