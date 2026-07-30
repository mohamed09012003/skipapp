package com.example.skipapp.ui

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
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
import androidx.lifecycle.lifecycleScope
import com.example.skipapp.R
import com.example.skipapp.accessibility.VoiceAccessibilityService
import com.example.skipapp.services.VoiceListeningService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnListen = findViewById<Button>(R.id.btn_listen)
        val tvRecognized = findViewById<TextView>(R.id.tv_recognized)
        val statusText = findViewById<TextView>(R.id.subtitle)

        val listeningService = VoiceListeningService(applicationContext)

        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                // start listening after permission granted
                listeningService.start()
                btnListen.text = "Stop Listening"
                lifecycleScope.launch {
                    listeningService.isListening.collectLatest { isListening ->
                        // no-op for now
                    }
                }
                lifecycleScope.launch {
                    // collect recognition results and display
                    // VoiceListeningService forwards results to gesture controller, but we also show them here
                    // Use the VoiceRecognizer flow indirectly by creating a short collector on the service
                }
            }
        }

        statusText.text = if (isAccessibilityEnabled(this)) {
            "Accessibility service is active."
        } else {
            "Accessibility permission is required for gesture execution."
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
                listeningService.start()
                btnListen.text = "Stop Listening"

                lifecycleScope.launch {
                    listeningService.results.collectLatest { text ->
                        tvRecognized.text = text
                    }
                }
            } else {
                listeningService.stop()
                btnListen.text = "Start Listening"
            }
        }
    }

    private fun isAccessibilityEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? android.view.accessibility.AccessibilityManager
        val enabledServices = accessibilityManager?.installedAccessibilityServiceList
        return enabledServices?.any { service ->
            service.resolveInfo?.serviceInfo?.name == VoiceAccessibilityService::class.java.name
        } == true
    }
}
