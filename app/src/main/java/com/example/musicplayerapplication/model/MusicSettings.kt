package com.example.musicplayerapplication.model

data class MusicSettings(
    val audioQuality: AudioQuality = AudioQuality.HIGH,
    val downloadQuality: AudioQuality = AudioQuality.MEDIUM,
    val streamOnWifiOnly: Boolean = false,
    val enableEqualizer: Boolean = false,
    val crossfadeDuration: Int = 0, // in seconds
    val gaplessPlayback: Boolean = true,
    val showLyrics: Boolean = true,
    val sleepTimerMinutes: Int = 0
)

enum class AudioQuality(val bitrate: Int) {
    LOW(96),
    MEDIUM(192),
    HIGH(320)
}
