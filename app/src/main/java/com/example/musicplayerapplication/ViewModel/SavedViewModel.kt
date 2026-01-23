package com.example.musicplayerapplication.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.SavedRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing saved/favorite songs
 * Follows MVVM architecture with Repository pattern
 */
class SavedViewModel(
    private val repository: SavedRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _savedSongs = MutableStateFlow<List<Song>>(emptyList())
    val savedSongs: StateFlow<List<Song>> = _savedSongs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount: StateFlow<Int> = _favoritesCount.asStateFlow()

    init {
        loadSavedSongs()
    }

    /**
     * Load all saved songs from Firebase
     */
    fun loadSavedSongs() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val songs = repository.getSavedSongs(userId)
                    _savedSongs.value = songs
                    _favoritesCount.value = songs.size
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load saved songs: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Add a song to favorites
     */
    fun addToFavorites(song: Song) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val success = repository.addToFavorites(userId, song)
                    if (success) {
                        // Reload saved songs
                        loadSavedSongs()
                    } else {
                        _errorMessage.value = "Failed to add to favorites"
                    }
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error adding to favorites: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    /**
     * Remove a song from favorites
     */
    fun removeFromFavorites(songId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val success = repository.removeFromFavorites(userId, songId)
                    if (success) {
                        // Reload saved songs
                        loadSavedSongs()
                    } else {
                        _errorMessage.value = "Failed to remove from favorites"
                    }
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error removing from favorites: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    /**
     * Toggle favorite status of a song
     */
    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val isFav = repository.isFavorite(userId, song.id)
                    if (isFav) {
                        removeFromFavorites(song.id)
                    } else {
                        addToFavorites(song)
                    }
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error toggling favorite: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    /**
     * Check if a song is in favorites
     */
    suspend fun isFavorite(songId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                repository.isFavorite(userId, songId)
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Clear all favorites
     */
    fun clearAllFavorites() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val success = repository.clearAllFavorites(userId)
                    if (success) {
                        _savedSongs.value = emptyList()
                        _favoritesCount.value = 0
                    } else {
                        _errorMessage.value = "Failed to clear favorites"
                    }
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error clearing favorites: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh favorites count
     */
    fun refreshFavoritesCount() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val count = repository.getFavoritesCount(userId)
                    _favoritesCount.value = count
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
