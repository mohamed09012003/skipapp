package com.example.skipapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skipapp.R
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.skipapp.services.VoiceListeningService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnListen = findViewById<Button>(R.id.btn_listen)
        val tvRecognized = findViewById<TextView>(R.id.tv_recognized)

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

        btnListen.setOnClickListener {
            val has = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            if (!has) {
                requestPermission.launch(Manifest.permission.RECORD_AUDIO)
                return@setOnClickListener
            }

            if (btnListen.text.contains("Start")) {
                listeningService.start()
                btnListen.text = "Stop Listening"

                // Display recognized strings by tapping into the recognizer via a one-off coroutine
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
}
