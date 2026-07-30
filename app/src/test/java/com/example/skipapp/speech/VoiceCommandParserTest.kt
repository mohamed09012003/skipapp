package com.example.skipapp.speech

import com.example.skipapp.commands.Command
import org.junit.Assert.assertEquals
import org.junit.Test

class VoiceCommandParserTest {
    private val parser = VoiceCommandParser()

    @Test
    fun parsesSkipAliases() {
        assertEquals(Command.Skip, parser.parse("skip"))
        assertEquals(Command.Skip, parser.parse("NEXT"))
        assertEquals(Command.Skip, parser.parse("next video"))
        assertEquals(Command.Skip, parser.parse("pass"))
        assertEquals(Command.Skip, parser.parse("keep scrolling"))
        assertEquals(Command.Skip, parser.parse("continue"))
    }

    @Test
    fun parsesBackAliases() {
        assertEquals(Command.Back, parser.parse("back"))
        assertEquals(Command.Back, parser.parse("previous"))
        assertEquals(Command.Back, parser.parse("previous video"))
        assertEquals(Command.Back, parser.parse("go back"))
        assertEquals(Command.Back, parser.parse("last video"))
    }

    @Test
    fun parsesPauseAliases() {
        assertEquals(Command.Pause, parser.parse("pause"))
        assertEquals(Command.Pause, parser.parse("stop"))
        assertEquals(Command.Pause, parser.parse("play"))
    }

    @Test
    fun parsesLikeAliases() {
        assertEquals(Command.Like, parser.parse("like"))
        assertEquals(Command.Like, parser.parse("heart"))
        assertEquals(Command.Like, parser.parse("favorite"))
    }

    @Test
    fun parsesMuteAliases() {
        assertEquals(Command.Mute, parser.parse("mute"))
        assertEquals(Command.Mute, parser.parse("silent"))
    }

    @Test
    fun parsesUnmuteAliases() {
        assertEquals(Command.Unmute, parser.parse("unmute"))
        assertEquals(Command.Unmute, parser.parse("sound on"))
    }

    @Test
    fun parsesScrollSpeedAliases() {
        assertEquals(Command.ScrollFaster, parser.parse("faster"))
        assertEquals(Command.ScrollFaster, parser.parse("speed up"))
        assertEquals(Command.ScrollSlower, parser.parse("slower"))
        assertEquals(Command.ScrollSlower, parser.parse("slow down"))
    }

    @Test
    fun parsesListeningControlAliases() {
        assertEquals(Command.EnableListening, parser.parse("start listening"))
        assertEquals(Command.EnableListening, parser.parse("wake up"))
        assertEquals(Command.EnableListening, parser.parse("activate"))
        assertEquals(Command.DisableListening, parser.parse("stop listening"))
        assertEquals(Command.DisableListening, parser.parse("sleep"))
        assertEquals(Command.DisableListening, parser.parse("deactivate"))
    }

    @Test
    fun trimsWhitespaceAndHandlesCase() {
        assertEquals(Command.Skip, parser.parse("  NEXT VIDEO  "))
        assertEquals(Command.Back, parser.parse("   previous video   "))
        assertEquals(Command.Pause, parser.parse("  Play   "))
    }

    @Test
    fun returnsUnknownForUnrecognizedInput() {
        assertEquals(Command.Unknown, parser.parse("random words"))
    }
}
