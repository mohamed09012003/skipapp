package com.example.skipapp.services

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.example.skipapp.accessibility.GestureController
import com.example.skipapp.commands.Command
import com.example.skipapp.commands.CommandRegistry
import com.example.skipapp.speech.VoiceCommandParser
import com.example.skipapp.speech.VoiceRecognizer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

class VoiceListeningService(
    private val context: Context,
    private val gestureController: GestureController? = null,
    private val commandRegistry: CommandRegistry = CommandRegistry(),
    private val parser: VoiceCommandParser = VoiceCommandParser()
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val recognizer = VoiceRecognizer(context)
    private var lastVolume: Int? = null
    private val tag = "VoiceListeningService"
    val results = recognizer.results
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun start() {
        recognizer.start()
        _isListening.value = true
        scope.launch {
            try {
                recognizer.results.collect { text ->
                    handleCommand(text)
                }
            } catch (_: CancellationException) {
            }
        }
    }

    fun stop() {
        scope.coroutineContext.cancelChildren()
        recognizer.stop()
        _isListening.value = false
    }

    fun handleCommand(rawCommand: String) {
        val command = parser.parse(rawCommand)
        val controller = gestureController ?: run {
            return
        }
        when (command) {
            Command.Skip -> controller.swipeUp()
            Command.Back -> controller.swipeDown()
            Command.Pause -> controller.tapCenter()
            Command.Like -> controller.doubleTap()
            Command.Mute -> toggleMute(true)
            Command.Unmute -> toggleMute(false)
            Command.ScrollFaster -> Unit
            Command.ScrollSlower -> Unit
            Command.EnableListening -> Unit
            Command.DisableListening -> Unit
            Command.Unknown -> Unit
        }
    }

    private fun toggleMute(mute: Boolean) {
        val manager = audioManager ?: run {
            Log.w(tag, "AudioManager unavailable; mute/unmute not applied")
            return
        }

        try {
            if (mute) {
                lastVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC)
                manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                Log.d(tag, "Muted device audio")
            } else {
                val volumeToRestore = lastVolume ?: manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2
                manager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeToRestore, 0)
                Log.d(tag, "Restored device audio volume")
            }
        } catch (t: Throwable) {
            Log.w(tag, "Mute/unmute failed", t)
        }
    }

    fun availableCommands() = commandRegistry.all()
}
