package com.example.musicplayerapplication.ViewModel

import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.model.AudioQuality
import com.example.musicplayerapplication.model.MusicSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _settings = MutableStateFlow(MusicSettings())
    val settings: StateFlow<MusicSettings> = _settings.asStateFlow()

    fun updateAudioQuality(quality: AudioQuality) {
        _settings.value = _settings.value.copy(audioQuality = quality)
        // TODO: Save to SharedPreferences
    }

    fun updateDownloadQuality(quality: AudioQuality) {
        _settings.value = _settings.value.copy(downloadQuality = quality)
        // TODO: Save to SharedPreferences
    }

    fun updateStreamOnWifiOnly(enabled: Boolean) {
        _settings.value = _settings.value.copy(streamOnWifiOnly = enabled)
        // TODO: Save to SharedPreferences
    }

    fun updateEnableEqualizer(enabled: Boolean) {
        _settings.value = _settings.value.copy(enableEqualizer = enabled)
        // TODO: Save to SharedPreferences
    }

    fun updateGaplessPlayback(enabled: Boolean) {
        _settings.value = _settings.value.copy(gaplessPlayback = enabled)
        // TODO: Save to SharedPreferences
    }

    fun updateShowLyrics(enabled: Boolean) {
        _settings.value = _settings.value.copy(showLyrics = enabled)
        // TODO: Save to SharedPreferences
    }

    fun updateCrossfadeDuration(duration: Int) {
        _settings.value = _settings.value.copy(crossfadeDuration = duration)
        // TODO: Save to SharedPreferences
    }

    fun updateSleepTimer(minutes: Int) {
        _settings.value = _settings.value.copy(sleepTimerMinutes = minutes)
        // TODO: Save to SharedPreferences
    }
}

