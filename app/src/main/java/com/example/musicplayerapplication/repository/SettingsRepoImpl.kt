package com.example.musicplayerapplication.repository

import android.content.Context
import com.example.musicplayerapplication.Utils.PreferencesManager
import com.example.musicplayerapplication.model.AudioQuality
import com.example.musicplayerapplication.model.MusicSettings
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementation of SettingsRepository
 * Manages settings persistence using SharedPreferences and Firebase sync
 */
class SettingsRepoImpl(private val context: Context) : SettingsRepository {

    private val prefsManager = PreferencesManager.getInstance(context)
    private val database = FirebaseDatabase.getInstance()
    private val settingsRef = database.getReference("userSettings")

    override fun getSettingsFlow(): Flow<MusicSettings> = callbackFlow {
        // Initial emit
        trySend(prefsManager.getMusicSettings())

        // Listen for changes (you can implement a SharedPreferences listener here)
        // For simplicity, we emit the current value
        awaitClose {}
    }

    override suspend fun getSettings(): MusicSettings {
        return prefsManager.getMusicSettings()
    }

    override suspend fun updateAudioQuality(quality: AudioQuality) {
        prefsManager.updateAudioQuality(quality)
    }

    override suspend fun updateDownloadQuality(quality: AudioQuality) {
        prefsManager.updateDownloadQuality(quality)
    }

    override suspend fun updateStreamOnWifiOnly(enabled: Boolean) {
        prefsManager.updateStreamOnWifiOnly(enabled)
    }

    override suspend fun updateEnableEqualizer(enabled: Boolean) {
        prefsManager.updateEnableEqualizer(enabled)
    }

    override suspend fun updateCrossfadeDuration(duration: Int) {
        prefsManager.updateCrossfadeDuration(duration)
    }

    override suspend fun updateGaplessPlayback(enabled: Boolean) {
        prefsManager.updateGaplessPlayback(enabled)
    }

    override suspend fun updateShowLyrics(enabled: Boolean) {
        prefsManager.updateShowLyrics(enabled)
    }

    override suspend fun updateSleepTimer(minutes: Int) {
        prefsManager.updateSleepTimer(minutes)
    }

    override suspend fun saveSettings(settings: MusicSettings) {
        prefsManager.saveMusicSettings(settings)
    }

    override suspend fun syncSettingsToFirebase(userId: String) {
        try {
            val settings = prefsManager.getMusicSettings()
            val settingsMap = mapOf(
                "audioQuality" to settings.audioQuality.name,
                "downloadQuality" to settings.downloadQuality.name,
                "streamOnWifiOnly" to settings.streamOnWifiOnly,
                "enableEqualizer" to settings.enableEqualizer,
                "crossfadeDuration" to settings.crossfadeDuration,
                "gaplessPlayback" to settings.gaplessPlayback,
                "showLyrics" to settings.showLyrics,
                "sleepTimerMinutes" to settings.sleepTimerMinutes
            )
            settingsRef.child(userId).setValue(settingsMap).await()
        } catch (e: Exception) {
            // Handle error - settings not synced to cloud
            e.printStackTrace()
        }
    }

    override suspend fun syncSettingsFromFirebase(userId: String): MusicSettings? {
        return try {
            val snapshot = settingsRef.child(userId).get().await()
            if (snapshot.exists()) {
                val audioQuality = snapshot.child("audioQuality").getValue<String>()
                    ?.let { AudioQuality.valueOf(it) } ?: AudioQuality.HIGH
                val downloadQuality = snapshot.child("downloadQuality").getValue<String>()
                    ?.let { AudioQuality.valueOf(it) } ?: AudioQuality.MEDIUM

                val settings = MusicSettings(
                    audioQuality = audioQuality,
                    downloadQuality = downloadQuality,
                    streamOnWifiOnly = snapshot.child("streamOnWifiOnly").getValue<Boolean>() ?: false,
                    enableEqualizer = snapshot.child("enableEqualizer").getValue<Boolean>() ?: false,
                    crossfadeDuration = snapshot.child("crossfadeDuration").getValue<Int>() ?: 0,
                    gaplessPlayback = snapshot.child("gaplessPlayback").getValue<Boolean>() ?: true,
                    showLyrics = snapshot.child("showLyrics").getValue<Boolean>() ?: true,
                    sleepTimerMinutes = snapshot.child("sleepTimerMinutes").getValue<Int>() ?: 0
                )

                // Save to local preferences
                prefsManager.saveMusicSettings(settings)
                settings
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun clearSettings() {
        prefsManager.clearAllSettings()
    }

    override suspend fun deleteUserDataFromFirebase(userId: String) {
        try {
            settingsRef.child(userId).removeValue().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
