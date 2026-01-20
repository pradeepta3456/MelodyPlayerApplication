package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.PlaylistRepoImpl
import com.example.musicplayerapplication.repository.PlaylistRepository
import com.example.musicplayerapplication.viewmodel.PlaylistModel
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val repository: PlaylistRepository = PlaylistRepoImpl()
) : ViewModel() {

    val playlists = mutableStateListOf<Playlist>()
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    init {
        loadPlaylists()
    }

    /**
     * Load all playlists
     */
    private fun loadPlaylists() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                playlists.clear()
                playlists.addAll(repository.getAllPlaylists())
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = "Failed to load playlists"
                isLoading.value = false
            }
        }
    }

    /**
     * Create new playlist
     */
    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            try {
                val newPlaylist = com.example.musicplayerapplication.model.Playlist(
                    id = (playlists.maxOfOrNull { it.id } ?: 0) + 1,
                    name = name,
                    description = description
                )
                repository.createPlaylist(newPlaylist)
                playlists.add(newPlaylist)
            } catch (e: Exception) {
                errorMessage.value = "Failed to create playlist"
            }
        }
    }

    /**
     * Delete playlist
     */
    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            try {
                repository.deletePlaylist(playlistId)
                playlists.removeIf { it.id == playlistId }
            } catch (e: Exception) {
                errorMessage.value = "Failed to delete playlist"
            }
        }
    }

    /**
     * Add song to playlist
     */
    fun addSongToPlaylist(playlistId: Int, songId: Int) {
        viewModelScope.launch {
            try {
                repository.addSongToPlaylist(playlistId, songId)
            } catch (e: Exception) {
                errorMessage.value = "Failed to add song"
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

