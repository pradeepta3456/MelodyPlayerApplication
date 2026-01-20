package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.repository.LibraryRepo
import com.example.musicplayerapplication.repository.LibraryRepoImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: LibraryRepo = LibraryRepoImpl()
) : ViewModel() {

    val artists = mutableStateListOf<LibraryArtist>()
    val selectedCategory = mutableStateOf("Albums")
    var isScanning = mutableStateOf(false)
    var scanProgress = mutableStateOf(0f)
    var errorMessage = mutableStateOf<String?>(null)

    init {
        loadArtists()
    }

    /**
     * Load artists from repository
     */
    private fun loadArtists() {
        viewModelScope.launch {
            try {
                artists.clear()
                artists.addAll(repository.getArtists())
            } catch (e: Exception) {
                errorMessage.value = "Failed to load artists"
            }
        }
    }

    /**
     * Scan device for music files
     */
    fun scanDeviceForMusic() {
        viewModelScope.launch {
            isScanning.value = true
            scanProgress.value = 0f

            try {
                // Simulate scanning process
                for (i in 1..10) {
                    delay(300)
                    scanProgress.value = i / 10f
                }

                // In real app: repository.scanDevice()
                loadArtists()
                isScanning.value = false
            } catch (e: Exception) {
                errorMessage.value = "Failed to scan device"
                isScanning.value = false
            }
        }
    }

    /**
     * Search artists by query
     */
    fun searchArtists(query: String): List<LibraryArtist> {
        return if (query.isBlank()) {
            artists
        } else {
            artists.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }

    /**
     * Select category
     */
    fun selectCategory(category: String) {
        selectedCategory.value = category
        // Load data for selected category
        when (category) {
            "Songs" -> loadSongs()
            "Albums" -> loadArtists()
            "Artists" -> loadArtists()
            "Genres" -> loadGenres()
            "Folders" -> loadFolders()
        }
    }

    private fun loadSongs() {
        // Load songs from repository
    }

    private fun loadGenres() {
        // Load genres from repository
    }

    private fun loadFolders() {
        // Load folders from repository
    }

    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage.value = null
    }
}
