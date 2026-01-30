package com.example.musicplayerapplication.Utils

import android.content.Context
import android.content.SharedPreferences
import com.example.musicplayerapplication.model.AudioQuality
import com.example.musicplayerapplication.model.MusicSettings

/**
 * Manager class for handling app preferences using SharedPreferences
 * Follows singleton pattern for easy access throughout the app
 */
class PreferencesManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "melody_player_prefs"

        // Settings keys
        private const val KEY_AUDIO_QUALITY = "audio_quality"
        private const val KEY_DOWNLOAD_QUALITY = "download_quality"
        private const val KEY_STREAM_WIFI_ONLY = "stream_wifi_only"
        private const val KEY_ENABLE_EQUALIZER = "enable_equalizer"
        private const val KEY_CROSSFADE_DURATION = "crossfade_duration"
        private const val KEY_GAPLESS_PLAYBACK = "gapless_playback"
        private const val KEY_SHOW_LYRICS = "show_lyrics"
        private const val KEY_SLEEP_TIMER = "sleep_timer"

        // Audio effects keys
        private const val KEY_BASS_LEVEL = "bass_level"
        private const val KEY_TREBLE_LEVEL = "treble_level"
        private const val KEY_VOLUME_LEVEL = "volume_level"
        private const val KEY_REVERB_ENABLED = "reverb_enabled"
        private const val KEY_REVERB_LEVEL = "reverb_level"
        private const val KEY_EQUALIZER_PRESET = "equalizer_preset"
        private const val KEY_EQUALIZER_BANDS = "equalizer_bands"

        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    // ==================== Music Settings ====================

    fun saveMusicSettings(settings: MusicSettings) {
        prefs.edit().apply {
            putString(KEY_AUDIO_QUALITY, settings.audioQuality.name)
            putString(KEY_DOWNLOAD_QUALITY, settings.downloadQuality.name)
            putBoolean(KEY_STREAM_WIFI_ONLY, settings.streamOnWifiOnly)
            putBoolean(KEY_ENABLE_EQUALIZER, settings.enableEqualizer)
            putInt(KEY_CROSSFADE_DURATION, settings.crossfadeDuration)
            putBoolean(KEY_GAPLESS_PLAYBACK, settings.gaplessPlayback)
            putBoolean(KEY_SHOW_LYRICS, settings.showLyrics)
            putInt(KEY_SLEEP_TIMER, settings.sleepTimerMinutes)
            apply()
        }
    }

    fun getMusicSettings(): MusicSettings {
        return MusicSettings(
            audioQuality = AudioQuality.valueOf(
                prefs.getString(KEY_AUDIO_QUALITY, AudioQuality.HIGH.name) ?: AudioQuality.HIGH.name
            ),
            downloadQuality = AudioQuality.valueOf(
                prefs.getString(KEY_DOWNLOAD_QUALITY, AudioQuality.MEDIUM.name) ?: AudioQuality.MEDIUM.name
            ),
            streamOnWifiOnly = prefs.getBoolean(KEY_STREAM_WIFI_ONLY, false),
            enableEqualizer = prefs.getBoolean(KEY_ENABLE_EQUALIZER, false),
            crossfadeDuration = prefs.getInt(KEY_CROSSFADE_DURATION, 0),
            gaplessPlayback = prefs.getBoolean(KEY_GAPLESS_PLAYBACK, true),
            showLyrics = prefs.getBoolean(KEY_SHOW_LYRICS, true),
            sleepTimerMinutes = prefs.getInt(KEY_SLEEP_TIMER, 0)
        )
    }

    fun updateAudioQuality(quality: AudioQuality) {
        prefs.edit().putString(KEY_AUDIO_QUALITY, quality.name).apply()
    }

    fun updateDownloadQuality(quality: AudioQuality) {
        prefs.edit().putString(KEY_DOWNLOAD_QUALITY, quality.name).apply()
    }

    fun updateStreamOnWifiOnly(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_STREAM_WIFI_ONLY, enabled).apply()
    }

    fun updateEnableEqualizer(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLE_EQUALIZER, enabled).apply()
    }

    fun updateCrossfadeDuration(duration: Int) {
        prefs.edit().putInt(KEY_CROSSFADE_DURATION, duration).apply()
    }

    fun updateGaplessPlayback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_GAPLESS_PLAYBACK, enabled).apply()
    }

    fun updateShowLyrics(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_LYRICS, enabled).apply()
    }

    fun updateSleepTimer(minutes: Int) {
        prefs.edit().putInt(KEY_SLEEP_TIMER, minutes).apply()
    }

    // ==================== Audio Effects Settings ====================

    fun saveBassLevel(level: Float) {
        prefs.edit().putFloat(KEY_BASS_LEVEL, level).apply()
    }

    fun getBassLevel(): Float {
        return prefs.getFloat(KEY_BASS_LEVEL, 0f)
    }

    fun saveTrebleLevel(level: Float) {
        prefs.edit().putFloat(KEY_TREBLE_LEVEL, level).apply()
    }

    fun getTrebleLevel(): Float {
        return prefs.getFloat(KEY_TREBLE_LEVEL, 0f)
    }

    fun saveVolumeLevel(level: Float) {
        prefs.edit().putFloat(KEY_VOLUME_LEVEL, level).apply()
    }

    fun getVolumeLevel(): Float {
        return prefs.getFloat(KEY_VOLUME_LEVEL, 0.7f)
    }

    fun saveReverbEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REVERB_ENABLED, enabled).apply()
    }

    fun getReverbEnabled(): Boolean {
        return prefs.getBoolean(KEY_REVERB_ENABLED, false)
    }

    fun saveReverbLevel(level: Float) {
        prefs.edit().putFloat(KEY_REVERB_LEVEL, level).apply()
    }

    fun getReverbLevel(): Float {
        return prefs.getFloat(KEY_REVERB_LEVEL, 0f)
    }

    fun saveEqualizerPreset(preset: String) {
        prefs.edit().putString(KEY_EQUALIZER_PRESET, preset).apply()
    }

    fun getEqualizerPreset(): String {
        return prefs.getString(KEY_EQUALIZER_PRESET, "FLAT") ?: "FLAT"
    }

    fun saveEqualizerBands(bands: FloatArray) {
        // Convert FloatArray to comma-separated string
        val bandsString = bands.joinToString(",")
        prefs.edit().putString(KEY_EQUALIZER_BANDS, bandsString).apply()
    }

    fun getEqualizerBands(): FloatArray {
        val bandsString = prefs.getString(KEY_EQUALIZER_BANDS, null)
        return if (bandsString != null) {
            bandsString.split(",").map { it.toFloatOrNull() ?: 0f }.toFloatArray()
        } else {
            FloatArray(5) { 0f } // Default 5-band equalizer with all bands at 0
        }
    }

    // ==================== Utility Methods ====================

    fun clearAllSettings() {
        prefs.edit().clear().apply()
    }

    fun clearAudioEffects() {
        prefs.edit().apply {
            remove(KEY_BASS_LEVEL)
            remove(KEY_TREBLE_LEVEL)
            remove(KEY_REVERB_ENABLED)
            remove(KEY_REVERB_LEVEL)
            remove(KEY_EQUALIZER_PRESET)
            remove(KEY_EQUALIZER_BANDS)
            apply()
        }
    }
}
