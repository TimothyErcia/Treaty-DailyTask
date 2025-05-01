package com.treaty.dailytask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.repository.reminder.ReminderRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class MenuViewModel(private val reminderRepositoryImpl: ReminderRepositoryImpl) : ViewModel() {
    private var _menuNotificationToggle = MutableStateFlow(false)
    var menuNotificationToggle = _menuNotificationToggle.asStateFlow()

    private var _menuTime = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    var menuTime = _menuTime
        .onStart { getReminderTime() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LocalDateTime.now())

    fun notificationToggle() {
        _menuNotificationToggle.value = !_menuNotificationToggle.value
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepositoryImpl.updateReminderTrigger(
                _menuNotificationToggle.value,
                _menuTime.value
            )
        }
    }

    suspend fun getReminderTime() {
        val repo = reminderRepositoryImpl.getReminderStatus()
        val zoneOffset = ZonedDateTime.of(
            _menuTime.value.toLocalDate(),
            _menuTime.value.toLocalTime(),
            ZoneId.of("Asia/Manila")
        ).offset
        _menuTime.emit(LocalDateTime.ofEpochSecond(repo.dateOfTrigger, 0, zoneOffset))
        _menuNotificationToggle.emit(repo.isToggled)
    }

    fun updateReminderTime(currentTime: LocalDateTime) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepositoryImpl.updateReminderTrigger(true, currentTime)
        }
        _menuNotificationToggle.value = true
    }
}
