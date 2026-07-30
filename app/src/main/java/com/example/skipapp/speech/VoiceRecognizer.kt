package com.example.skipapp.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class VoiceRecognizer(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var _results = Channel<String>(Channel.BUFFERED)
    val results: Flow<String> get() = _results.receiveAsFlow()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isStarted = false

    fun start() {
        if (isStarted) return
        if (SpeechRecognizer.isRecognitionAvailable(context).not()) return

        _results = Channel(Channel.BUFFERED)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    val intent = listeningIntent()
                    startListening(intent)
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: return
                    scope.launch { _results.send(text) }
                    val intent = listeningIntent()
                    startListening(intent)
                }
            })
        }

        isStarted = true
        val intent = listeningIntent()
        startListening(intent)
    }

    private fun listeningIntent(): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
    }

    private fun startListening(intent: Intent) {
        try {
            speechRecognizer?.startListening(intent)
        } catch (_: Exception) {
        }
    }

    fun stop() {
        if (!isStarted) return
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isStarted = false
    }
}
