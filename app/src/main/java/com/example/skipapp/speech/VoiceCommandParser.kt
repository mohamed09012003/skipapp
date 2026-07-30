package com.example.skipapp.speech

import com.example.skipapp.commands.Command

class VoiceCommandParser {
    fun parse(rawText: String): Command {
        val normalized = sanitize(rawText)

        return when {
            normalized == "skip" || normalized == "next" || normalized == "next video" || normalized == "pass" || normalized == "keep scrolling" || normalized == "continue" -> Command.Skip
            normalized == "back" || normalized == "previous" || normalized == "previous video" || normalized == "go back" || normalized == "last video" -> Command.Back
            normalized == "pause" || normalized == "stop" || normalized == "play" -> Command.Pause
            normalized == "like" || normalized == "heart" || normalized == "favorite" -> Command.Like
            normalized == "mute" || normalized == "silent" -> Command.Mute
            normalized == "unmute" || normalized == "sound on" -> Command.Unmute
            normalized == "faster" || normalized == "speed up" -> Command.ScrollFaster
            normalized == "slower" || normalized == "slow down" -> Command.ScrollSlower
            normalized == "start listening" || normalized == "wake up" || normalized == "activate" -> Command.EnableListening
            normalized == "stop listening" || normalized == "sleep" || normalized == "deactivate" -> Command.DisableListening
            else -> Command.Unknown
        }
    }

    private fun sanitize(rawText: String): String {
        return rawText.trim().lowercase().replace(Regex("\\s+"), " ")
    }
}
