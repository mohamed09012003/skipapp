package com.example.skipapp.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class VoiceAccessibilityService : AccessibilityService() {
    override fun onInterrupt() = Unit

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
}
