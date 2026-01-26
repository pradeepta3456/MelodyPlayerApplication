package com.example.musicplayerapplication.model
data class UserPreferences(
    val theme: String = "dark",
    val language: String = "en",
    val audioQuality: String = "high",
    val downloadQuality: String = "high",
    val autoPlay: Boolean = true,
    val crossfade: Boolean = false,
    val gaplessPlayback: Boolean = true,
    val normalizeVolume: Boolean = false,
    val showExplicitContent: Boolean = true,
    val privateSession: Boolean = false,
    val dataSaver: Boolean = false,
    val downloadOverWifiOnly: Boolean = true
) {

    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "theme" to theme,
            "language" to language,
            "audioQuality" to audioQuality,
            "downloadQuality" to downloadQuality,
            "autoPlay" to autoPlay,
            "crossfade" to crossfade,
            "gaplessPlayback" to gaplessPlayback,
            "normalizeVolume" to normalizeVolume,
            "showExplicitContent" to showExplicitContent,
            "privateSession" to privateSession,
            "dataSaver" to dataSaver,
            "downloadOverWifiOnly" to downloadOverWifiOnly
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UserPreferences {
            return UserPreferences(
                theme = map["theme"] as? String ?: "dark",
                language = map["language"] as? String ?: "en",
                audioQuality = map["audioQuality"] as? String ?: "high",
                downloadQuality = map["downloadQuality"] as? String ?: "high",
                autoPlay = map["autoPlay"] as? Boolean ?: true,
                crossfade = map["crossfade"] as? Boolean ?: false,
                gaplessPlayback = map["gaplessPlayback"] as? Boolean ?: true,
                normalizeVolume = map["normalizeVolume"] as? Boolean ?: false,
                showExplicitContent = map["showExplicitContent"] as? Boolean ?: true,
                privateSession = map["privateSession"] as? Boolean ?: false,
                dataSaver = map["dataSaver"] as? Boolean ?: false,
                downloadOverWifiOnly = map["downloadOverWifiOnly"] as? Boolean ?: true
            )
        }
    }
}

