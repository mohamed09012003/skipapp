package com.example.skipapp.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
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
        if (SpeechRecognizer.isRecognitionAvailable(context).not()) {
            Log.w("VoiceRecognizer", "Speech recognition unavailable")
            return
        }

        _results = Channel(Channel.BUFFERED)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("VoiceRecognizer", "Ready for speech")
                }
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull()
                    if (!text.isNullOrBlank()) {
                        Log.d("VoiceRecognizer", "Partial: $text")
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onBeginningOfSpeech() {
                    Log.d("VoiceRecognizer", "Beginning of speech")
                }
                override fun onEndOfSpeech() {
                    Log.d("VoiceRecognizer", "End of speech")
                }

                override fun onError(error: Int) {
                    Log.w("VoiceRecognizer", "Recognition error: $error")
                    scope.launch {
                        _results.send("error:$error")
                    }
                    val intent = listeningIntent()
                    startListening(intent)
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: return
                    Log.d("VoiceRecognizer", "Recognized: $text")
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
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command")
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
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
