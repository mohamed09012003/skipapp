package com.example.skipapp.speech

fun main() {
    val parser = VoiceCommandParser()
    println(parser.parse("sound on"))
    println(parser.parse("start listening"))
    println(parser.parse("stop listening"))
}
