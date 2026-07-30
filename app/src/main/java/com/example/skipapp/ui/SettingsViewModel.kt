package com.example.skipapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skipapp.settings.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val voiceControlEnabled: Boolean = true,
    val backgroundListeningEnabled: Boolean = false,
    val accessibilityStatus: String = "Unknown",
    val microphoneStatus: String = "Unknown",
    val swipeSpeed: Int = 3,
    val commandSensitivity: Int = 3,
    val wakeWord: String = "Hey Skip"
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = UserPreferences(application.applicationContext)

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.voiceControlEnabled.collect { enabled ->
                _state.value = _state.value.copy(voiceControlEnabled = enabled)
            }
        }
        viewModelScope.launch {
            preferences.backgroundListeningEnabled.collect { enabled ->
                _state.value = _state.value.copy(backgroundListeningEnabled = enabled)
            }
        }
        viewModelScope.launch {
            preferences.swipeSpeed.collect { speed ->
                _state.value = _state.value.copy(swipeSpeed = speed)
            }
        }
        viewModelScope.launch {
            preferences.commandSensitivity.collect { sensitivity ->
                _state.value = _state.value.copy(commandSensitivity = sensitivity)
            }
        }
        viewModelScope.launch {
            preferences.wakeWord.collect { word ->
                _state.value = _state.value.copy(wakeWord = word)
            }
        }
    }

    fun setVoiceControlEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.setVoiceControlEnabled(enabled) }
    }

    fun setBackgroundListeningEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.setBackgroundListeningEnabled(enabled) }
    }

    fun setSwipeSpeed(speed: Int) {
        viewModelScope.launch { preferences.setSwipeSpeed(speed) }
    }

    fun setCommandSensitivity(sensitivity: Int) {
        viewModelScope.launch { preferences.setCommandSensitivity(sensitivity) }
    }

    fun setWakeWord(word: String) {
        viewModelScope.launch { preferences.setWakeWord(word) }
    }

    fun setAccessibilityStatus(status: String) {
        _state.value = _state.value.copy(accessibilityStatus = status)
    }

    fun setMicrophoneStatus(status: String) {
        _state.value = _state.value.copy(microphoneStatus = status)
    }
}
