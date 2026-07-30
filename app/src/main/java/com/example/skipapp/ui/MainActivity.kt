package com.example.skipapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.skipapp.R
import com.example.skipapp.accessibility.VoiceAccessibilityService
import com.example.skipapp.services.VoiceListeningForegroundService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnListen = findViewById<Button>(R.id.btn_listen)
        val btnSettings = findViewById<Button>(R.id.btn_settings)
        val tvRecognized = findViewById<TextView>(R.id.tv_recognized)
        val statusText = findViewById<TextView>(R.id.subtitle)

        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startListeningService()
                btnListen.text = "Stop Listening"
            }
        }

        statusText.text = if (isAccessibilityEnabled(this)) {
            "Accessibility service is active."
        } else {
            "Accessibility permission is required for gesture execution."
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnListen.setOnClickListener {
            val has = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            if (!has) {
                requestPermission.launch(Manifest.permission.RECORD_AUDIO)
                return@setOnClickListener
            }

            if (!isAccessibilityEnabled(this)) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                statusText.text = "Enable the accessibility permission to allow gestures."
                return@setOnClickListener
            }

            if (btnListen.text.contains("Start")) {
                startListeningService()
                btnListen.text = "Stop Listening"
            } else {
                stopListeningService()
                btnListen.text = "Start Listening"
            }
        }
    }

    private fun startListeningService() {
        val serviceIntent = Intent(this, VoiceListeningForegroundService::class.java).apply {
            putExtra(VoiceListeningForegroundService.EXTRA_START, true)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopListeningService() {
        val serviceIntent = Intent(this, VoiceListeningForegroundService::class.java).apply {
            putExtra(VoiceListeningForegroundService.EXTRA_START, false)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun isAccessibilityEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? android.view.accessibility.AccessibilityManager
        val enabledServices = accessibilityManager?.installedAccessibilityServiceList
        return enabledServices?.any { service ->
            service.resolveInfo?.serviceInfo?.name == VoiceAccessibilityService::class.java.name
        } == true
    }
}
