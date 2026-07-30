package com.example.skipapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skipapp.R
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        val switchVoiceControl = findViewById<MaterialSwitch>(R.id.switch_voice_control)
        val switchBackgroundListening = findViewById<MaterialSwitch>(R.id.switch_background_listening)
        val statusAccessibility = findViewById<TextView>(R.id.status_accessibility)
        val statusMicrophone = findViewById<TextView>(R.id.status_microphone)
        val seekSwipeSpeed = findViewById<SeekBar>(R.id.seek_swipe_speed)
        val seekSensitivity = findViewById<SeekBar>(R.id.seek_command_sensitivity)
        val editWakeWord = findViewById<EditText>(R.id.edit_wake_word)

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                switchVoiceControl.isChecked = state.voiceControlEnabled
                switchBackgroundListening.isChecked = state.backgroundListeningEnabled
                statusAccessibility.text = "Accessibility: ${state.accessibilityStatus}"
                statusMicrophone.text = "Microphone: ${state.microphoneStatus}"
                seekSwipeSpeed.progress = state.swipeSpeed - 1
                seekSensitivity.progress = state.commandSensitivity - 1
                editWakeWord.setText(state.wakeWord)
            }
        }

        switchVoiceControl.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setVoiceControlEnabled(isChecked)
        }
        switchBackgroundListening.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setBackgroundListeningEnabled(isChecked)
        }
        seekSwipeSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.setSwipeSpeed(progress + 1)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekSensitivity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.setCommandSensitivity(progress + 1)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        editWakeWord.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) viewModel.setWakeWord(editWakeWord.text.toString())
        }

        updateStatusValues()
    }

    private fun updateStatusValues() {
        val accessibilityEnabled = isAccessibilityEnabled(this)
        viewModel.setAccessibilityStatus(if (accessibilityEnabled) "Enabled" else "Disabled")

        val microphoneGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        viewModel.setMicrophoneStatus(if (microphoneGranted) "Granted" else "Required")
    }

    private fun isAccessibilityEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? android.view.accessibility.AccessibilityManager
        val enabledServices = accessibilityManager?.installedAccessibilityServiceList
        return enabledServices?.any { service ->
            service.resolveInfo?.serviceInfo?.name == com.example.skipapp.accessibility.VoiceAccessibilityService::class.java.name
        } == true
    }
}
