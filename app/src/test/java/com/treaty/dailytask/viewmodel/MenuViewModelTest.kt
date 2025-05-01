package com.treaty.dailytask.viewmodel

import com.treaty.dailytask.repository.FakeRepository
import com.treaty.dailytask.repository.reminder.ReminderRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var menuViewModel: MenuViewModel
    private lateinit var reminderRepositoryImpl: ReminderRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        reminderRepositoryImpl = ReminderRepositoryImpl(FakeRepository.FakeReminderDAO())
        menuViewModel = MenuViewModel(reminderRepositoryImpl)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should initialize with getReminderTime`() = runTest(testDispatcher) {
        menuViewModel.getReminderTime()
        assertTrue(menuViewModel.menuNotificationToggle.value)
        assertEquals(menuViewModel.menuTime.value.year, LocalDate.now().year)
    }

    @Test
    fun `should call notificationToggle and update value`() = runTest(testDispatcher) {
        assertTrue(menuViewModel.menuNotificationToggle.value)
        menuViewModel.notificationToggle()
        assertFalse(menuViewModel.menuNotificationToggle.value)
    }

    @Test
    fun `should call updateReminderTime`() = runTest(testDispatcher) {
        menuViewModel.updateReminderTime(LocalDateTime.now())
        assertTrue(menuViewModel.menuNotificationToggle.value)
    }

}