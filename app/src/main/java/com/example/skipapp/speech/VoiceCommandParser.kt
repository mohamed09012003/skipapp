package com.example.skipapp.speech

import com.example.skipapp.commands.Command

class VoiceCommandParser {
    fun parse(rawText: String): Command {
        val normalized = sanitize(rawText)
        val compact = normalized.replace(Regex("[^a-z0-9 ]"), "")
        val tokens = compact.split(Regex("\\s+")).filter { it.isNotBlank() }

        return when {
            compact.contains("skip") || compact.contains("next") || compact.contains("pass") || compact.contains("continue") -> Command.Skip
            compact.contains("back") || compact.contains("previous") || compact.contains("go back") || compact.contains("last video") -> Command.Back
            compact.contains("pause") || compact.contains("stop") || compact.contains("play") -> Command.Pause
            compact.contains("like") || compact.contains("heart") || compact.contains("favorite") -> Command.Like
            compact.contains("mute") || compact.contains("silent") -> Command.Mute
            compact.contains("unmute") || compact.contains("sound on") -> Command.Unmute
            compact.contains("faster") || compact.contains("speed up") -> Command.ScrollFaster
            compact.contains("slower") || compact.contains("slow down") -> Command.ScrollSlower
            compact.contains("start listening") || compact.contains("wake up") || compact.contains("activate") -> Command.EnableListening
            compact.contains("stop listening") || compact.contains("sleep") || compact.contains("deactivate") -> Command.DisableListening
            else -> Command.Unknown
        }
    }

    private fun sanitize(rawText: String): String {
        return rawText.trim().lowercase().replace(Regex("\\s+"), " ")
    }
}
