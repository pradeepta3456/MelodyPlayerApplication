package com.example.musicplayerapplication.Utils

import android.media.MediaPlayer
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Audio effects manager for MediaPlayer
 * Handles Equalizer, Bass Boost, and Reverb effects
 */
class AudioEffects(private val mediaPlayer: MediaPlayer) {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var presetReverb: PresetReverb? = null

    private val _bassLevel = MutableStateFlow(0f) // -10 to +10 dB
    val bassLevel: StateFlow<Float> = _bassLevel

    private val _trebleLevel = MutableStateFlow(0f) // -10 to +10 dB
    val trebleLevel: StateFlow<Float> = _trebleLevel

    private val _reverbEnabled = MutableStateFlow(false)
    val reverbEnabled: StateFlow<Boolean> = _reverbEnabled

    private val _reverbLevel = MutableStateFlow(0f) // 0 to 100
    val reverbLevel: StateFlow<Float> = _reverbLevel

    private val _equalizerBands = MutableStateFlow(FloatArray(5) { 0f })
    val equalizerBands: StateFlow<FloatArray> = _equalizerBands

    init {
        try {
            initializeEffects()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeEffects() {
        try {
            val audioSessionId = mediaPlayer.audioSessionId

            // Initialize Equalizer
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }

            // Initialize Bass Boost
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = true
            }

            // Initialize Reverb
            presetReverb = PresetReverb(0, audioSessionId).apply {
                enabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Set bass level (-10 to +10 dB)
     */
    fun setBassLevel(level: Float) {
        try {
            bassBoost?.let {
                // Bass boost strength ranges from 0 to 1000
                // Convert -10 to +10 dB range to 0 to 1000
                val strength = ((level + 10f) / 20f * 1000f).toInt().coerceIn(0, 1000)
                it.setStrength(strength.toShort())
                _bassLevel.value = level
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Set treble level using equalizer's high frequency bands
     */
    fun setTrebleLevel(level: Float) {
        try {
            equalizer?.let { eq ->
                val numberOfBands = eq.numberOfBands.toInt()
                if (numberOfBands > 0) {
                    // Apply to upper frequency bands (treble)
                    val upperBands = numberOfBands / 2
                    val maxLevel = eq.bandLevelRange[1] // max level in millibels
                    val minLevel = eq.bandLevelRange[0] // min level in millibels

                    // Convert -10 to +10 dB to equalizer range
                    val targetLevel = ((level / 10f) * maxLevel).toInt().toShort()

                    for (i in upperBands until numberOfBands) {
                        eq.setBandLevel(i.toShort(), targetLevel)
                    }
                    _trebleLevel.value = level
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Enable/disable reverb effect
     */
    fun setReverbEnabled(enabled: Boolean) {
        try {
            presetReverb?.enabled = enabled
            _reverbEnabled.value = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Set reverb preset level (0-100 mapped to preset types)
     */
    fun setReverbLevel(level: Float) {
        try {
            presetReverb?.let {
                // Map 0-100 to reverb presets
                val preset = when {
                    level <= 20f -> PresetReverb.PRESET_NONE
                    level <= 40f -> PresetReverb.PRESET_SMALLROOM
                    level <= 60f -> PresetReverb.PRESET_MEDIUMROOM
                    level <= 80f -> PresetReverb.PRESET_LARGEROOM
                    else -> PresetReverb.PRESET_PLATE
                }
                it.preset = preset.toShort()
                _reverbLevel.value = level
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Set individual equalizer band level
     * @param band Band index (0 to numberOfBands-1)
     * @param level Level in dB (-10 to +10)
     */
    fun setEqualizerBand(band: Int, level: Float) {
        try {
            equalizer?.let { eq ->
                val maxLevel = eq.bandLevelRange[1]
                val targetLevel = ((level / 10f) * maxLevel).toInt().toShort()
                eq.setBandLevel(band.toShort(), targetLevel)

                // Update state
                val currentBands = _equalizerBands.value.copyOf()
                if (band < currentBands.size) {
                    currentBands[band] = level
                    _equalizerBands.value = currentBands
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Apply equalizer preset
     */
    fun applyEqualizerPreset(preset: EqualizerPreset) {
        try {
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

            bands.forEachIndexed { index, level ->
                setEqualizerBand(index, level)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get equalizer frequency for a specific band
     */
    fun getBandFrequency(band: Int): Int {
        return try {
            equalizer?.getCenterFreq(band.toShort())?.div(1000) ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get number of equalizer bands
     */
    fun getNumberOfBands(): Int {
        return try {
            equalizer?.numberOfBands?.toInt() ?: 5
        } catch (e: Exception) {
            5
        }
    }

    /**
     * Release all audio effects
     */
    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
            presetReverb?.release()
            equalizer = null
            bassBoost = null
            presetReverb = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Predefined equalizer presets
 */
enum class EqualizerPreset {
    FLAT,
    BASS_BOOST,
    TREBLE_BOOST,
    ROCK,
    POP,
    JAZZ,
    CLASSICAL,
    VOCAL
}
