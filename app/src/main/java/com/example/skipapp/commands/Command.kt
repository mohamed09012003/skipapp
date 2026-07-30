package com.example.skipapp.commands

sealed class Command {
    data object Skip : Command()
    data object Back : Command()
    data object Pause : Command()
    data object Like : Command()
    data object Mute : Command()
    data object Unmute : Command()
    data object ScrollFaster : Command()
    data object ScrollSlower : Command()
    data object EnableListening : Command()
    data object DisableListening : Command()
    data object Unknown : Command()
}
