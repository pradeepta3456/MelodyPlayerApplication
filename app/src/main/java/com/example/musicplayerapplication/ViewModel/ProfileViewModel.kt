package com.example.musicplayerapplication.ViewModel

import Artist
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.model.UserStats
import com.example.musicplayerapplication.repository.ProfileRepo
import com.example.musicplayerapplication.repository.ProfileRepoImpl
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepo = ProfileRepoImpl()
) : ViewModel() {

    val topSongs: List<Song> = repository.getTopSongs()
    val topArtists: List<Artist> = repository.getTopArtists()
    val achievements: List<Achievement> = repository.getAchievements()
    var userStats = mutableStateOf(UserStats())
    var errorMessage = mutableStateOf<String?>(null)

    init {
        loadUserStats()
    }

    /**
     * Load user statistics
     */
    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                userStats.value = UserStats(
                    listeningTime = "1247h",
                    songsPlayed = 3421,
                    topGenre = "Electronic",
                    dayStreak = 45
                )
            } catch (e: Exception) {
                errorMessage.value = "Failed to load stats"
            }
        }
    }

    /**
     * Update user profile
     */
    fun updateProfile(displayName: String, profileImageUrl: String) {
        viewModelScope.launch {
            try {
                repository.updateProfile(displayName, profileImageUrl)
            } catch (e: Exception) {
                errorMessage.value = "Failed to update profile"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage.value = null
    }
}
