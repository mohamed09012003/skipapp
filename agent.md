# AGENT.md

# VoiceScroll AI - Project Agent Instructions

## Project Overview

VoiceScroll AI is an Android application that enables hands-free control of short-form video apps (such as TikTok and Instagram Reels) using voice commands.

The app continuously listens for predefined voice commands and performs Android accessibility gestures to emulate user interactions such as swiping, tapping, and scrolling.

The application **does not modify or interact with TikTok or Instagram internally.** It only performs user-like gestures through Android's Accessibility Service.

---

# Primary Goals

The application should:

- Continuously listen for voice commands while enabled.
- Work in the background.
- Use Android's AccessibilityService to perform gestures.
- Be lightweight and battery efficient.
- Minimize latency between speech and action.
- Prioritize on-device speech recognition whenever available.
- Be modular and easy to extend with new commands.

---

# Tech Stack

Language:
- Kotlin

Minimum SDK:
- Android 8.0 (API 26)

Recommended Target SDK:
- Latest stable Android SDK

Architecture:
- MVVM
- Repository Pattern
- Kotlin Coroutines
- StateFlow

Libraries:
- AndroidX
- Lifecycle
- Navigation
- Kotlin Coroutines
- Material 3
- SpeechRecognizer API
- AccessibilityService API

---

# Project Structure

app/

    accessibility/
        VoiceAccessibilityService.kt
        GestureController.kt

    speech/
        VoiceRecognizer.kt
        VoiceCommandParser.kt

    commands/
        Command.kt
        CommandRegistry.kt

    services/
        VoiceListeningService.kt

    ui/
        MainActivity.kt
        SettingsScreen.kt
        PermissionScreen.kt

    settings/
        UserPreferences.kt

    utils/
        Logger.kt
        Constants.kt

---

# Supported Apps

Primary support:

- TikTok
- Instagram Reels

Future support:

- YouTube Shorts
- Facebook Reels
- Snapchat Spotlight

The architecture should allow adding more apps without changing core logic.

---

# Voice Commands

## Navigation

Skip

Aliases:

- skip
- next
- next video
- pass
- keep scrolling
- continue

Action:

Swipe Up

---

Back

Aliases:

- back
- previous
- last video
- go back

Action:

Swipe Down

---

Pause

Aliases:

- pause
- play
- stop

Action:

Tap center of screen

The app should remember playback state.

---

Like

Aliases:

- like
- heart
- favorite

Action:

Double tap video area.

---

Mute

Aliases:

- mute
- silent

Action:

Tap volume region if available.

---

Unmute

Aliases:

- unmute
- sound on

Action:

Reverse mute gesture.

---

Scroll Faster

Aliases:

- faster
- speed up

Action:

Decrease swipe duration.

---

Scroll Slower

Aliases:

- slower
- slow down

Action:

Increase swipe duration.

---

Enable Listening

Aliases:

- start listening
- wake up
- activate

Action:

Enable microphone listener.

---

Disable Listening

Aliases:

- stop listening
- sleep
- deactivate

Action:

Disable listener while app remains running.

---

# Accessibility Service

Responsible for:

- Swipe Up
- Swipe Down
- Tap
- Double Tap
- Long Press (future)
- Custom Gestures

Must never attempt to inspect or manipulate private data inside supported apps.

Only simulate user gestures.

---

# Speech Recognition

Requirements

- Continuous listening
- Automatic restart after results
- Automatic restart after timeout
- Automatic restart after errors
- Ignore low confidence results
- Debounce duplicate commands

Target latency:

<500ms

---

# Gesture Settings

Configurable values:

Swipe distance

Swipe duration

Tap duration

Double tap interval

Gesture delay

Users should be able to customize these settings.

---

# Settings Screen

Options

Enable Voice Control

Enable Background Listening

Wake Word

Command Sensitivity

Swipe Speed

Dark Mode

Supported Apps

Accessibility Status

Microphone Status

Battery Optimization Status

---

# Future AI Features

Potential additions:

Natural language understanding

Examples:

"Skip this"

"I don't like this one"

"Go to the next"

"Scroll"

"Take me back"

These should map automatically to commands.

---

Context awareness.

Example:

User:

"Again"

System understands:

Repeat previous command.

---

Custom commands

Example:

User defines:

"boring"

Maps to

Swipe Up

---

Multiple languages

English

Spanish

German

French

Japanese

etc.

---

# Performance Goals

Memory:

<100MB

Cold startup:

<2 seconds

Voice detection:

<500ms

Gesture execution:

<200ms

Battery usage:

Minimal

---

# Security

The app:

Never records conversations permanently.

Never uploads voice unless the user explicitly enables cloud recognition.

Never stores microphone audio.

Only stores:

User settings

Command mappings

Preferences

---

# Code Standards

Use Kotlin best practices.

Avoid large classes.

Maximum function length:

50 lines

Maximum class length:

400 lines

Prefer immutable data.

Prefer composition over inheritance.

Use dependency injection where appropriate.

Document public methods.

Write meaningful variable names.

No magic numbers.

Use constants.

---

# Testing

Unit Tests

Voice command parser

Command registry

Gesture builder

Settings

Integration Tests

Speech → Command

Command → Gesture

Accessibility Service

UI Tests

Permission flow

Settings

Accessibility enable flow

---

# Error Handling

Handle:

Microphone unavailable

Permission denied

Accessibility disabled

Speech timeout

Recognition failure

Unsupported device

Battery optimization restrictions

The app should recover automatically whenever possible.

---

# Non-Goals

The application will NOT:

Bypass platform security.

Interact with private APIs.

Inject code into other apps.

Automate account actions beyond user-equivalent gestures.

Violate Google Play accessibility policies.

---

# Development Principles

1. Keep the app responsive.

2. Favor reliability over complexity.

3. Design every feature to be modular.

4. Keep command recognition extensible.

5. Minimize battery consumption.

6. Make gesture execution deterministic.

7. Ensure the app degrades gracefully if permissions are revoked.

8. Maintain clean architecture and comprehensive documentation.

---

# Vision

VoiceScroll AI should feel like a hands-free remote control for short-form video apps. The user should be able to consume content without touching the screen, using fast, reliable voice commands that trigger natural Android gestures through accessibility services.