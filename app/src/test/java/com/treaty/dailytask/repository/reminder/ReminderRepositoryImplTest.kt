package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ReminderRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var reminderRepositoryImpl: ReminderRepositoryImpl

    @Mock
    private lateinit var reminderDAO: ReminderRepository

    @Before
    fun setUp() {
        reminderRepositoryImpl = ReminderRepositoryImpl(reminderDAO)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getReminderStatus returns Reminder`() = runTest(testDispatcher) {
        `when`(reminderDAO.getReminderStatus()).thenReturn(Result.success(Reminder(true, 0L)))
        reminderRepositoryImpl.updateReminderTrigger(true, LocalDateTime.now())
        val reminder = reminderRepositoryImpl.getReminderStatus()
        assertTrue(reminder.isToggled)
    }

    @Test
    fun `updateReminderTrigger with true parameter`() = runTest(testDispatcher) {
        val reminder = Reminder(true, 0L)
        reminderRepositoryImpl.updateReminderTrigger(true, LocalDateTime.now())

        assertTrue(reminder.isToggled)
    }

    @Test
    fun `updateReminderTrigger with false paramenter`() = runTest(testDispatcher) {
        val reminder = Reminder(false, 0L)
        reminderRepositoryImpl.updateReminderTrigger(false, LocalDateTime.now())

        assertFalse(reminder.isToggled)
    }

    @Test
    fun `getReminderStatus returns default`() = runTest(testDispatcher) {
        `when`(reminderDAO.getReminderStatus()).thenReturn(Result.failure(Throwable("Error")))
        val offset = ZonedDateTime.of(
            LocalDate.now(),
            LocalTime.now(),
            ZoneId.of("Asia/Manila")
        ).offset
        val reminder = reminderRepositoryImpl.getReminderStatus()
        assertFalse(reminder.isToggled)
        assertTrue(reminder.dateOfTrigger >= LocalDateTime.now().toEpochSecond(offset))
    }
}
