package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.HomeRepo
import com.example.musicplayerapplication.repository.HomeRepoImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeViewModel(
    private val repository: HomeRepo = HomeRepoImpl()
) : ViewModel() {

    // State
    val recentSongs: List<Song> = repository.getRecentSongs()
    val trendingAlbums: List<Album> = repository.getTrendingAlbums()
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    /**
     * Refresh data from repository
     */
    fun refreshData() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                delay(1000) // Simulate network delay
                // In real app: repository.refreshData()
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message
                isLoading.value = false
            }
        }
    }

    /**
     * Toggle favorite status for a song
     */
    fun toggleFavorite(songId: Int) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(songId)
            } catch (e: Exception) {
                errorMessage.value = "Failed to update favorite"
            }
        }
    }

    /**
     * Play a song
     */
    fun playSong(songId: Int) {
        viewModelScope.launch {
            try {
                repository.playSong(songId)
            } catch (e: Exception) {
                errorMessage.value = "Failed to play song"
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
