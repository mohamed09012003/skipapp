package com.example.skipapp.speech

class VoiceCommandParser {
    fun parse(rawText: String): String = rawText.trim().lowercase()
}
