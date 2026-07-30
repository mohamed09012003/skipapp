package com.example.skipapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.skipapp.R
import com.example.skipapp.accessibility.GestureController
import com.example.skipapp.accessibility.VoiceAccessibilityService
import com.example.skipapp.commands.Command
import com.example.skipapp.commands.CommandRegistry
import com.example.skipapp.speech.VoiceCommandParser
import com.example.skipapp.speech.VoiceRecognizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VoiceListeningForegroundService : Service() {
    private val tag = "VoiceListeningForegroundService"
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var recognizer: VoiceRecognizer
    private lateinit var parser: VoiceCommandParser
    private lateinit var commandRegistry: CommandRegistry
    private lateinit var gestureController: GestureController
    private val isListeningState = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> get() = isListeningState

    override fun onCreate() {
        super.onCreate()
        recognizer = VoiceRecognizer(this)
        parser = VoiceCommandParser()
        commandRegistry = CommandRegistry()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val shouldStart = intent?.getBooleanExtra(EXTRA_START, false) ?: true
        if (shouldStart) {
            startForeground(NOTIFICATION_ID, buildNotification())
            startListening()
        } else {
            stopListening()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startListening() {
        if (isListeningState.value) return
        recognizer.start()
        isListeningState.value = true
        gestureController = GestureController(VoiceAccessibilityService.instance)
        scope.launch {
            recognizer.results.collect { text ->
                Log.d(tag, "Received command: $text")
                handleCommand(text)
            }
        }
        updateNotification()
    }

    private fun stopListening() {
        if (!isListeningState.value) return
        recognizer.stop()
        isListeningState.value = false
        updateNotification()
    }

    private fun handleCommand(rawCommand: String) {
        sendStatus(rawCommand)
        when (parser.parse(rawCommand)) {
            com.example.skipapp.commands.Command.Skip -> gestureController.swipeUp()
            com.example.skipapp.commands.Command.Back -> gestureController.swipeDown()
            com.example.skipapp.commands.Command.Pause -> gestureController.tapCenter()
            com.example.skipapp.commands.Command.Like -> gestureController.doubleTap()
            com.example.skipapp.commands.Command.Mute -> toggleMute(true)
            com.example.skipapp.commands.Command.Unmute -> toggleMute(false)
            else -> Unit
        }
    }

    private fun sendStatus(text: String) {
        val intent = Intent(ACTION_STATUS_UPDATE).apply {
            putExtra(EXTRA_LAST_COMMAND, text)
        }
        sendBroadcast(intent)
    }

    private fun toggleMute(mute: Boolean) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
        if (audioManager == null) {
            Log.w(tag, "AudioManager unavailable")
            return
        }
        try {
            if (mute) {
                audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, 0, 0)
            } else {
                audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC) / 2, 0)
            }
        } catch (t: Throwable) {
            Log.w(tag, "Mute/unmute failed", t)
        }
    }

    private fun buildNotification(): android.app.Notification {
        val intent = Intent(this, com.example.skipapp.ui.MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Voice control active")
            .setContentText("Listening for commands in the background")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification() {
        val notification = buildNotification()
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Voice control", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        recognizer.stop()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.example.skipapp.action.START_VOICE_LISTENING"
        const val ACTION_STOP = "com.example.skipapp.action.STOP_VOICE_LISTENING"
        const val ACTION_STATUS_UPDATE = "com.example.skipapp.action.STATUS_UPDATE"
        const val EXTRA_START = "extra_start"
        const val EXTRA_LAST_COMMAND = "extra_last_command"
        const val CHANNEL_ID = "voice_listening_channel"
        const val NOTIFICATION_ID = 1001
    }
}
