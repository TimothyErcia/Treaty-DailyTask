package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import io.realm.kotlin.query.RealmResults

interface ReminderRepository {
    suspend fun updateReminderTrigger(reminder: Reminder)

    suspend fun getReminderStatus(): RealmResults<Reminder>
}
