package com.example.musicplayerapplication.repository

import android.content.Context
import com.example.musicplayerapplication.Utils.PreferencesManager

/**
 * Implementation of AudioEffectsRepository
 * Manages audio effects persistence using SharedPreferences
 */
class AudioEffectsRepoImpl(context: Context) : AudioEffectsRepository {

    private val prefsManager = PreferencesManager.getInstance(context)

    override suspend fun setBassLevel(level: Float) {
        prefsManager.saveBassLevel(level)
    }

    override suspend fun getBassLevel(): Float {
        return prefsManager.getBassLevel()
    }

    override suspend fun setTrebleLevel(level: Float) {
        prefsManager.saveTrebleLevel(level)
    }

    override suspend fun getTrebleLevel(): Float {
        return prefsManager.getTrebleLevel()
    }

    override suspend fun setVolumeLevel(level: Float) {
        prefsManager.saveVolumeLevel(level)
    }

    override suspend fun getVolumeLevel(): Float {
        return prefsManager.getVolumeLevel()
    }

    override suspend fun setReverbEnabled(enabled: Boolean) {
        prefsManager.saveReverbEnabled(enabled)
    }

    override suspend fun getReverbEnabled(): Boolean {
        return prefsManager.getReverbEnabled()
    }

    override suspend fun setReverbLevel(level: Float) {
        prefsManager.saveReverbLevel(level)
    }

    override suspend fun getReverbLevel(): Float {
        return prefsManager.getReverbLevel()
    }

    override suspend fun setEqualizerPreset(preset: String) {
        prefsManager.saveEqualizerPreset(preset)
    }

    override suspend fun getEqualizerPreset(): String {
        return prefsManager.getEqualizerPreset()
    }

    override suspend fun setEqualizerBands(bands: FloatArray) {
        prefsManager.saveEqualizerBands(bands)
    }

    override suspend fun getEqualizerBands(): FloatArray {
        return prefsManager.getEqualizerBands()
    }

    override suspend fun resetAudioEffects() {
        prefsManager.clearAudioEffects()
    }
}
