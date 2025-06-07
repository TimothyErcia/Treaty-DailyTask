package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ReminderRepositoryImpl(private val reminderDAO: ReminderRepository) {
    private val offset = ZonedDateTime.of(
        LocalDate.now(),
        LocalTime.now(),
        ZoneId.of("Asia/Manila")
    ).offset

    suspend fun getReminderStatus(): Reminder {
        val result = reminderDAO.getReminderStatus()
        val reminder =
            result.getOrDefault(Reminder(false, LocalDateTime.now().toLong()))
        return reminder!!
    }

    suspend fun updateReminderTrigger(notificationToggle: Boolean, currentTime: LocalDateTime) {
        val reminder = Reminder(notificationToggle, currentTime.toLong())
        if (notificationToggle) {
            reminderDAO.updateReminderTrigger(reminder, currentTime)
        } else {
            reminderDAO.stopReminder()
        }
    }

    private fun LocalDateTime.toLong(): Long {
        return this.toEpochSecond(offset)
    }

    fun toLocalDateTime(epoch: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(epoch, 0, offset)
    }
}
