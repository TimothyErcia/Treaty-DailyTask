package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder

class ReminderRepositoryImpl(private val reminderDAO: ReminderRepository) {
    suspend fun getReminderStatus(): Reminder? {
        return reminderDAO.getReminderStatus().getOrNull(0)
    }

    suspend fun updateReminderTrigger(reminder: Reminder) {
        reminderDAO.updateReminderTrigger(reminder)
    }
}
