package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReminderRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val realmConfiguration = RealmConfiguration.Builder(schema = setOf(Reminder::class))
        .inMemory()
        .name("test-realm")
        .build()
    private val realm = Realm.open(realmConfiguration)
    private val reminderRepositoryImpl = ReminderRepositoryImpl(ReminderDAO(realm))

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test fun `getReminderStatus returns Reminder`() = runTest {
        reminderRepositoryImpl.updateReminderTrigger(Reminder(true))
        val reminder = reminderRepositoryImpl.getReminderStatus()

        reminder?.let {
            assertEquals(reminder.isCompleted, true)
        }
    }

    @Test fun `updateReminderTrigger with true paramenter`() = runTest {
        val reminder = Reminder(true)
        reminderRepositoryImpl.updateReminderTrigger(reminder)

        assertTrue(reminder.isCompleted)
    }

    @Test fun `updateReminderTrigger with false paramenter`() = runTest {
        val reminder = Reminder(false)
        reminderRepositoryImpl.updateReminderTrigger(reminder)

        assertFalse(reminder.isCompleted)
    }
}
