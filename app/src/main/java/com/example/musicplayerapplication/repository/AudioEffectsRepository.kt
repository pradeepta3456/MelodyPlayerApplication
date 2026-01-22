package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.Utils.EqualizerPreset
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for audio effects management
 */
interface AudioEffectsRepository {

    // Bass control
    suspend fun setBassLevel(level: Float)
    suspend fun getBassLevel(): Float

    // Treble control
    suspend fun setTrebleLevel(level: Float)
    suspend fun getTrebleLevel(): Float

    // Volume control
    suspend fun setVolumeLevel(level: Float)
    suspend fun getVolumeLevel(): Float

    // Reverb control
    suspend fun setReverbEnabled(enabled: Boolean)
    suspend fun getReverbEnabled(): Boolean
    suspend fun setReverbLevel(level: Float)
    suspend fun getReverbLevel(): Float

    // Equalizer control
    suspend fun setEqualizerPreset(preset: String)
    suspend fun getEqualizerPreset(): String
    suspend fun setEqualizerBands(bands: FloatArray)
    suspend fun getEqualizerBands(): FloatArray

    // Reset
    suspend fun resetAudioEffects()
}
