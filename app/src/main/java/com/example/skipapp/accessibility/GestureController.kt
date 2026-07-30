package com.example.skipapp.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class GestureController(private val service: AccessibilityService?) {
    private val tag = "GestureController"

    fun swipeUp() = performGesture(createSwipeGesture(0f, 0.2f, 0f, 0.8f, 300))

    fun swipeDown() = performGesture(createSwipeGesture(0f, 0.8f, 0f, 0.2f, 300))

    fun tapCenter() = performGesture(createTapGesture(0.5f, 0.5f, 150))

    fun doubleTap() = performGesture(createTapGesture(0.5f, 0.5f, 150), createTapGesture(0.5f, 0.5f, 150))

    private fun performGesture(vararg gestures: GestureDescription) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.w(tag, "Gesture skipped: SDK too old")
            return
        }
        val activeService = service ?: VoiceAccessibilityService.instance
        if (activeService == null) {
            Log.w(tag, "Gesture skipped: accessibility service unavailable")
            return
        }
        try {
            val dispatch = activeService.dispatchGesture(gestures.first(), null, null)
            if (!dispatch) {
                Log.w(tag, "Gesture dispatch returned false")
            }
        } catch (t: Throwable) {
            Log.w(tag, "Gesture execution failed", t)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createSwipeGesture(startX: Float, startY: Float, endX: Float, endY: Float, duration: Long): GestureDescription {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        return GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(path, 0, duration))
        }.build()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createTapGesture(x: Float, y: Float, duration: Long): GestureDescription {
        val path = Path().apply {
            moveTo(x, y)
            lineTo(x, y)
        }
        return GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(path, 0, duration))
        }.build()
    }
}
