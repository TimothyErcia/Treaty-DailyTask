package com.treaty.dailytask.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.Reminder
import com.treaty.dailytask.repository.reminder.ReminderRepositoryImpl
import com.treaty.dailytask.utility.AlarmUtility
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class MenuViewModel(
    private val reminderRepositoryImpl: ReminderRepositoryImpl,
    private val alarmUtility: AlarmUtility
) : ViewModel() {
    private var _menuNotificationToggle = MutableStateFlow(false)
    var menuNotificationToggle = _menuNotificationToggle.asStateFlow()

    private var _menuTime = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    var menuTime = _menuTime
        .onStart { getReminderTime() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LocalDateTime.now())

    fun notificationToggle() {
        _menuNotificationToggle.value = !_menuNotificationToggle.value
        if(_menuNotificationToggle.value) {
            alarmUtility.startAlarm()
        } else {
            alarmUtility.stopAlarm()
        }
    }

    private suspend fun getReminderTime() {
        val repo = reminderRepositoryImpl.getReminderStatus()
        if(repo != null) {
            val zoneOffset = ZonedDateTime.now().offset
            _menuTime.value = LocalDateTime.ofEpochSecond(repo.dateOfTrigger, 0, zoneOffset)
            _menuNotificationToggle.value = repo.isToggled
        }
    }

    fun updateReminderTime(currentTime: LocalDateTime) =
        viewModelScope.launch(Dispatchers.IO) {
            val offset = ZonedDateTime.of(currentTime.toLocalDate(), currentTime.toLocalTime(), ZoneId.of("Asia/Manila")).offset
            reminderRepositoryImpl.updateReminderTrigger(
                Reminder(_menuNotificationToggle.value, currentTime.toEpochSecond(offset)))
            alarmUtility.setAlarm(currentTime)
            alarmUtility.startAlarm()
            _menuNotificationToggle.value = true
        }
}
