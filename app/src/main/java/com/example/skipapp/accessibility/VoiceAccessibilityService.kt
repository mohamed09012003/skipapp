package com.example.skipapp.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class VoiceAccessibilityService : AccessibilityService() {
    private val tag = "VoiceAccessibilityService"
    val gestureController by lazy { GestureController(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(tag, "Accessibility service created")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(tag, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        Log.d(tag, "Accessibility event: ${event.eventType}")
    }

    override fun onInterrupt() {
        Log.w(tag, "Accessibility service interrupted")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(tag, "Accessibility service unbound")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    companion object {
        @Volatile
        var instance: VoiceAccessibilityService? = null
    }
}
