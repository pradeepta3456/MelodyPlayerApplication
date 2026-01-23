package com.example.musicplayerapplication.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.*
import com.example.musicplayerapplication.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for user profile and statistics
 * Follows MVVM architecture with Firebase Realtime Database
 */
class ProfileViewModel(
    private val repository: ProfileRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats.asStateFlow()

    private val _topSongs = MutableStateFlow<List<TopSong>>(emptyList())
    val topSongs: StateFlow<List<TopSong>> = _topSongs.asStateFlow()

    private val _topArtists = MutableStateFlow<List<TopArtist>>(emptyList())
    val topArtists: StateFlow<List<TopArtist>> = _topArtists.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _weeklyPattern = MutableStateFlow<List<WeeklyPattern>>(emptyList())
    val weeklyPattern: StateFlow<List<WeeklyPattern>> = _weeklyPattern.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadProfileData()
    }

    /**
     * Load all profile data from Firebase
     */
    fun loadProfileData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    // Load all data in parallel
                    launch { loadUserProfile(userId) }
                    launch { loadUserStats(userId) }
                    launch { loadTopSongs(userId) }
                    launch { loadTopArtists(userId) }
                    launch { loadAchievements(userId) }
                    launch { loadWeeklyPattern(userId) }
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load profile data: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadUserProfile(userId: String) {
        try {
            val profile = repository.getUserProfile(userId)
            _userProfile.value = profile ?: UserProfile(
                userId = userId,
                email = auth.currentUser?.email ?: "",
                displayName = "Music Lover"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadUserStats(userId: String) {
        try {
            val stats = repository.getUserStats(userId)
            _userStats.value = stats ?: UserStats()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadTopSongs(userId: String) {
        try {
            val songs = repository.getTopSongs(userId, 10)
            _topSongs.value = songs
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadTopArtists(userId: String) {
        try {
            val artists = repository.getTopArtists(userId, 4)
            _topArtists.value = artists
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadAchievements(userId: String) {
        try {
            val achievements = repository.getAchievements(userId)
            _achievements.value = achievements
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadWeeklyPattern(userId: String) {
        try {
            val pattern = repository.getWeeklyPattern(userId)
            _weeklyPattern.value = pattern
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Update user profile
     */
    fun updateProfile(displayName: String, bio: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val currentProfile = _userProfile.value ?: return@launch

                val updatedProfile = currentProfile.copy(
                    displayName = displayName,
                    bio = bio
                )

                val success = repository.updateUserProfile(userId, updatedProfile)
                if (success) {
                    _userProfile.value = updatedProfile
                } else {
                    _errorMessage.value = "Failed to update profile"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating profile: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    /**
     * Initialize user data (called on first login)
     */
    fun initializeUserData() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val email = auth.currentUser?.email ?: ""
                val displayName = auth.currentUser?.displayName ?: "Music Lover"

                repository.initializeUserData(userId, email, displayName)
                loadProfileData()
            } catch (e: Exception) {
                _errorMessage.value = "Error initializing user data: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    /**
     * Format listening time for display
     */
    fun getFormattedListeningTime(): String {
        val stats = _userStats.value ?: return "0h"
        val hours = stats.totalListeningTime / 3600000
        return if (hours > 0) "${hours}h" else "0h"
    }

    /**
     * Format member since date
     */
    fun getFormattedMemberSince(): String {
        val profile = _userProfile.value ?: return "Recently joined"
        val date = java.util.Date(profile.memberSince)
        val format = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
        return "Member since ${format.format(date)}"
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
