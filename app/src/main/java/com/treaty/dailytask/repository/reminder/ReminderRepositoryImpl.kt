package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ReminderRepositoryImpl(private val reminderDAO: ReminderRepository) {
    suspend fun getReminderStatus(): Reminder {
        val result = reminderDAO.getReminderStatus()
        val reminder = result.getOrDefault(Reminder(false, 0L))
        return reminder!!
    }

    suspend fun updateReminderTrigger(notificationToggle: Boolean, currentTime: LocalDateTime) {
        val offset = ZonedDateTime.of(
            currentTime.toLocalDate(),
            currentTime.toLocalTime(),
            ZoneId.of("Asia/Manila")
        ).offset
        val reminder = Reminder(notificationToggle, currentTime.toEpochSecond(offset))
        if (notificationToggle) {
            reminderDAO.updateReminderTrigger(reminder, currentTime)
        } else {
            reminderDAO.stopReminder()
        }
    }
}
