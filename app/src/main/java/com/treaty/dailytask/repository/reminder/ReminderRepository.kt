package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import java.time.LocalDateTime

interface ReminderRepository {
    suspend fun updateReminderTrigger(reminder: Reminder, currentTime: LocalDateTime)

    suspend fun getReminderStatus(): Result<Reminder?>

    fun stopReminder(): Result<String>
}
