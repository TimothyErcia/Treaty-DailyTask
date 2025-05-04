package com.treaty.dailytask.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ReminderTest {

    @Test
    fun verifyReminderModel() {
        val reminder = Reminder()
        assertEquals(reminder.reminderId, "Reminder Notification")
        assertFalse(reminder.isToggled)
        assertEquals(reminder.dateOfTrigger, 0L)
    }
}
