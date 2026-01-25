package com.example.musicplayerapplication.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.AudioQuality
import com.example.musicplayerapplication.model.MusicSettings
import com.example.musicplayerapplication.repository.SettingsRepository
import com.example.musicplayerapplication.repository.SettingsRepoImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing app settings
 * Follows MVVM architecture with Repository pattern
 */
class SettingsViewModel(
    private val repository: SettingsRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _settings = MutableStateFlow(MusicSettings())
    val settings: StateFlow<MusicSettings> = _settings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Load settings from local storage
     */
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val loadedSettings = repository.getSettings()
                _settings.value = loadedSettings
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sync settings from Firebase (useful on login or app start)
     */
    fun syncFromFirebase() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: return@launch
                val firebaseSettings = repository.syncSettingsFromFirebase(userId)
                if (firebaseSettings != null) {
                    _settings.value = firebaseSettings
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to sync settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sync settings to Firebase (useful for cross-device sync)
     */
    fun syncToFirebase() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                repository.syncSettingsToFirebase(userId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to sync settings to cloud: ${e.message}"
            }
        }
    }

    // ==================== Settings Update Methods ====================

    fun updateAudioQuality(quality: AudioQuality) {
        viewModelScope.launch {
            try {
                repository.updateAudioQuality(quality)
                _settings.value = _settings.value.copy(audioQuality = quality)
                syncToFirebase() // Optionally sync to cloud
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update audio quality"
            }
        }
    }

    fun updateDownloadQuality(quality: AudioQuality) {
        viewModelScope.launch {
            try {
                repository.updateDownloadQuality(quality)
                _settings.value = _settings.value.copy(downloadQuality = quality)
                syncToFirebase()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update download quality"
            }
        }
    }

    fun updateStreamOnWifiOnly(enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateStreamOnWifiOnly(enabled)
                _settings.value = _settings.value.copy(streamOnWifiOnly = enabled)
                syncToFirebase()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update WiFi setting"
            }
        }
    }

    fun updateEnableEqualizer(enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateEnableEqualizer(enabled)
                _settings.value = _settings.value.copy(enableEqualizer = enabled)
                syncToFirebase()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update equalizer setting"
            }
        }
    }

    fun updateGaplessPlayback(enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateGaplessPlayback(enabled)
                _settings.value = _settings.value.copy(gaplessPlayback = enabled)
                syncToFirebase()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update gapless playback"
            }
        }
    }

    fun updateShowLyrics(enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateShowLyrics(enabled)
                _settings.value = _settings.value.copy(showLyrics = enabled)
                syncToFirebase()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update lyrics setting"
            }
        }
    }

    fun updateCrossfadeDuration(duration: Int) {
        viewModelScope.launch {
            try {
                repository.updateCrossfadeDuration(duration)
                _settings.value = _settings.value.copy(crossfadeDuration = duration)
                syncToFirebase()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update crossfade duration"
            }
        }
    }

    fun updateSleepTimer(minutes: Int) {
        viewModelScope.launch {
            try {
                repository.updateSleepTimer(minutes)
                _settings.value = _settings.value.copy(sleepTimerMinutes = minutes)
                syncToFirebase()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update sleep timer"
            }
        }
    }

    // ==================== Auth Actions ====================

    /**
     * Logout user - moved from SettingsActivity for proper MVVM
     */
    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signOut()
                // Clear local settings
                repository.clearSettings()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Logout failed")
            }
        }
    }

    /**
     * Delete user account - moved from SettingsActivity for proper MVVM
     */
    fun deleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null) {
                    val userId = user.uid

                    // Delete user settings from Firebase Database first (ignore errors)
                    try {
                        repository.deleteUserDataFromFirebase(userId)
                    } catch (e: Exception) {
                        // Continue even if Firebase data deletion fails
                    }

                    // Clear local settings
                    repository.clearSettings()

                    // Delete from Firebase Auth using await() for proper coroutine handling
                    user.delete().await()
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("No user logged in")
                    }
                }
            } catch (e: com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                withContext(Dispatchers.Main) {
                    onError("Please sign out and sign in again before deleting your account")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Failed to delete account")
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
