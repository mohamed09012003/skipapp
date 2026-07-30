package com.example.skipapp.services

import android.content.Context
import com.example.skipapp.accessibility.GestureController
import com.example.skipapp.commands.CommandRegistry
import com.example.skipapp.speech.VoiceCommandParser
import com.example.skipapp.speech.VoiceRecognizer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

class VoiceListeningService(
    private val context: Context,
    private val gestureController: GestureController = GestureController(),
    private val commandRegistry: CommandRegistry = CommandRegistry(),
    private val parser: VoiceCommandParser = VoiceCommandParser()
) {
    private val recognizer = VoiceRecognizer(context)
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
        val normalized = parser.parse(rawCommand)
        if (normalized.contains("skip")) {
            gestureController.swipeUp()
        } else if (normalized.contains("back")) {
            gestureController.swipeDown()
        } else if (normalized.contains("pause")) {
            gestureController.tapCenter()
        } else if (normalized.contains("like")) {
            gestureController.doubleTap()
        }
    }

    fun availableCommands() = commandRegistry.all()
}
