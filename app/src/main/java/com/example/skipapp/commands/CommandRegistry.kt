package com.example.skipapp.commands

class CommandRegistry {
    fun all(): List<Command> = listOf(
        Command.Skip,
        Command.Back,
        Command.Pause,
        Command.Like,
        Command.Mute,
        Command.Unmute,
        Command.ScrollFaster,
        Command.ScrollSlower,
        Command.EnableListening,
        Command.DisableListening,
        Command.Unknown
    )
}
