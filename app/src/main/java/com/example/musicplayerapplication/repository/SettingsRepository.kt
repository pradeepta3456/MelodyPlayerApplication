package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.AudioQuality
import com.example.musicplayerapplication.model.MusicSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing app settings
 * Handles both local persistence (SharedPreferences) and cloud sync (Firebase)
 */
interface SettingsRepository {

    // Get settings as Flow for reactive UI updates
    fun getSettingsFlow(): Flow<MusicSettings>

    // Get settings synchronously
    suspend fun getSettings(): MusicSettings

    // Update individual settings
    suspend fun updateAudioQuality(quality: AudioQuality)
    suspend fun updateDownloadQuality(quality: AudioQuality)
    suspend fun updateStreamOnWifiOnly(enabled: Boolean)
    suspend fun updateEnableEqualizer(enabled: Boolean)
    suspend fun updateCrossfadeDuration(duration: Int)
    suspend fun updateGaplessPlayback(enabled: Boolean)
    suspend fun updateShowLyrics(enabled: Boolean)
    suspend fun updateSleepTimer(minutes: Int)

    // Bulk update
    suspend fun saveSettings(settings: MusicSettings)

    // Firebase sync (optional - for cross-device sync)
    suspend fun syncSettingsToFirebase(userId: String)
    suspend fun syncSettingsFromFirebase(userId: String): MusicSettings?

    // Logout/clear
    suspend fun clearSettings()
}
