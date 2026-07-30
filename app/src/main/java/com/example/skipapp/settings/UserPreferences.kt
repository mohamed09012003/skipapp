package com.example.skipapp.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    private val voiceControlKey = booleanPreferencesKey("voice_control_enabled")
    private val backgroundListeningKey = booleanPreferencesKey("background_listening_enabled")
    private val swipeSpeedKey = intPreferencesKey("swipe_speed")
    private val commandSensitivityKey = intPreferencesKey("command_sensitivity")
    private val wakeWordKey = stringPreferencesKey("wake_word")

    val voiceControlEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[voiceControlKey] ?: true
    }

    val backgroundListeningEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[backgroundListeningKey] ?: false
    }

    val swipeSpeed: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[swipeSpeedKey] ?: 3
    }

    val commandSensitivity: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[commandSensitivityKey] ?: 3
    }

    val wakeWord: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[wakeWordKey] ?: "Hey Skip"
    }

    suspend fun setVoiceControlEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[voiceControlKey] = enabled }
    }

    suspend fun setBackgroundListeningEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[backgroundListeningKey] = enabled }
    }

    suspend fun setSwipeSpeed(speed: Int) {
        context.dataStore.edit { prefs -> prefs[swipeSpeedKey] = speed.coerceIn(1, 5) }
    }

    suspend fun setCommandSensitivity(sensitivity: Int) {
        context.dataStore.edit { prefs -> prefs[commandSensitivityKey] = sensitivity.coerceIn(1, 5) }
    }

    suspend fun setWakeWord(word: String) {
        context.dataStore.edit { prefs -> prefs[wakeWordKey] = word.ifBlank { "Hey Skip" } }
    }
}
