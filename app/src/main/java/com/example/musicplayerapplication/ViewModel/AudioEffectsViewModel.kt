package com.example.musicplayerapplication.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.Utils.AudioPlayer
import com.example.musicplayerapplication.Utils.EqualizerPreset
import com.example.musicplayerapplication.repository.AudioEffectsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing audio effects
 * Follows MVVM architecture with Repository pattern
 */
class AudioEffectsViewModel(
    private val repository: AudioEffectsRepository,
    private val context: Context
) : ViewModel() {

    private val audioPlayer = AudioPlayer.getInstance(context)

    private val _bassLevel = MutableStateFlow(0f)
    val bassLevel: StateFlow<Float> = _bassLevel

    private val _trebleLevel = MutableStateFlow(0f)
    val trebleLevel: StateFlow<Float> = _trebleLevel

    private val _volumeLevel = MutableStateFlow(0.7f)
    val volumeLevel: StateFlow<Float> = _volumeLevel

    private val _reverbEnabled = MutableStateFlow(false)
    val reverbEnabled: StateFlow<Boolean> = _reverbEnabled

    private val _reverbLevel = MutableStateFlow(0f)
    val reverbLevel: StateFlow<Float> = _reverbLevel

    private val _equalizerPreset = MutableStateFlow("FLAT")
    val equalizerPreset: StateFlow<String> = _equalizerPreset

    private val _equalizerBands = MutableStateFlow(FloatArray(5) { 0f })
    val equalizerBands: StateFlow<FloatArray> = _equalizerBands

    init {
        loadAudioEffects()
    }

    /**
     * Load saved audio effects settings
     */
    private fun loadAudioEffects() {
        viewModelScope.launch {
            try {
                _bassLevel.value = repository.getBassLevel()
                _trebleLevel.value = repository.getTrebleLevel()
                _volumeLevel.value = repository.getVolumeLevel()
                _reverbEnabled.value = repository.getReverbEnabled()
                _reverbLevel.value = repository.getReverbLevel()
                _equalizerPreset.value = repository.getEqualizerPreset()
                _equalizerBands.value = repository.getEqualizerBands()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update bass level (-10 to +10 dB)
     */
    fun updateBassLevel(level: Float) {
        viewModelScope.launch {
            try {
                repository.setBassLevel(level)
                _bassLevel.value = level

                // Apply to audio player
                audioPlayer.getAudioEffects()?.setBassLevel(level)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update treble level (-10 to +10 dB)
     */
    fun updateTrebleLevel(level: Float) {
        viewModelScope.launch {
            try {
                repository.setTrebleLevel(level)
                _trebleLevel.value = level

                // Apply to audio player
                audioPlayer.getAudioEffects()?.setTrebleLevel(level)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update volume level (0.0 to 1.0)
     */
    fun updateVolumeLevel(level: Float) {
        viewModelScope.launch {
            try {
                repository.setVolumeLevel(level)
                _volumeLevel.value = level

                // Apply to audio player
                audioPlayer.setVolume(level)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Toggle reverb on/off
     */
    fun toggleReverb() {
        viewModelScope.launch {
            try {
                val newValue = !_reverbEnabled.value
                repository.setReverbEnabled(newValue)
                _reverbEnabled.value = newValue

                // Apply to audio player
                audioPlayer.getAudioEffects()?.setReverbEnabled(newValue)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update reverb level (0 to 100)
     */
    fun updateReverbLevel(level: Float) {
        viewModelScope.launch {
            try {
                repository.setReverbLevel(level)
                _reverbLevel.value = level

                // Apply to audio player
                audioPlayer.getAudioEffects()?.setReverbLevel(level)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Apply equalizer preset
     */
    fun applyEqualizerPreset(preset: EqualizerPreset) {
        viewModelScope.launch {
            try {
                repository.setEqualizerPreset(preset.name)
                _equalizerPreset.value = preset.name

                // Apply to audio player
                audioPlayer.getAudioEffects()?.applyEqualizerPreset(preset)

                // Update band levels after applying preset
                val bands = when (preset) {
                    EqualizerPreset.FLAT -> FloatArray(5) { 0f }
                    EqualizerPreset.BASS_BOOST -> floatArrayOf(8f, 6f, 3f, 0f, -1f)
                    EqualizerPreset.TREBLE_BOOST -> floatArrayOf(-1f, 0f, 3f, 6f, 8f)
                    EqualizerPreset.ROCK -> floatArrayOf(5f, 3f, -1f, 1f, 4f)
                    EqualizerPreset.POP -> floatArrayOf(-1f, 2f, 4f, 3f, -1f)
                    EqualizerPreset.JAZZ -> floatArrayOf(3f, 2f, 0f, 2f, 3f)
                    EqualizerPreset.CLASSICAL -> floatArrayOf(3f, 2f, -1f, 2f, 3f)
                    EqualizerPreset.VOCAL -> floatArrayOf(-1f, 0f, 3f, 4f, 2f)
                }
                _equalizerBands.value = bands
                repository.setEqualizerBands(bands)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update individual equalizer band
     */
    fun updateEqualizerBand(band: Int, level: Float) {
        viewModelScope.launch {
            try {
                val currentBands = _equalizerBands.value.copyOf()
                if (band < currentBands.size) {
                    currentBands[band] = level
                    _equalizerBands.value = currentBands
                    repository.setEqualizerBands(currentBands)

                    // Apply to audio player
                    audioPlayer.getAudioEffects()?.setEqualizerBand(band, level)

                    // Mark as custom preset
                    _equalizerPreset.value = "CUSTOM"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Reset all audio effects to defaults
     */
    fun resetAudioEffects() {
        viewModelScope.launch {
            try {
                repository.resetAudioEffects()
                _bassLevel.value = 0f
                _trebleLevel.value = 0f
                _volumeLevel.value = 0.7f
                _reverbEnabled.value = false
                _reverbLevel.value = 0f
                _equalizerPreset.value = "FLAT"
                _equalizerBands.value = FloatArray(5) { 0f }

                // Apply to audio player
                audioPlayer.getAudioEffects()?.apply {
                    setBassLevel(0f)
                    setTrebleLevel(0f)
                    setReverbEnabled(false)
                    applyEqualizerPreset(EqualizerPreset.FLAT)
                }
                audioPlayer.setVolume(0.7f)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
