package com.example.skipapp.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserPreferences {
    private val _voiceControlEnabled = MutableStateFlow(true)
    val voiceControlEnabled: StateFlow<Boolean> = _voiceControlEnabled

    fun setVoiceControlEnabled(enabled: Boolean) {
        _voiceControlEnabled.value = enabled
    }
}
