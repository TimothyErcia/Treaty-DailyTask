package com.treaty.dailytask.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel : ViewModel() {

    private var _menuToggle = MutableStateFlow(false)
    var mutableToggle = _menuToggle.asStateFlow()

    fun openMenu() {
        _menuToggle.value = true
    }

    fun closeMenu() {
        _menuToggle.value = false
    }
}
