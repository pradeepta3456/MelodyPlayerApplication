package com.example.musicplayerapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.model.PlaylistModel
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.PlaylistRepoImpl
import com.example.musicplayerapplication.repository.PlaylistRepository

class PlaylistViewModel(
    private val repository: PlaylistRepository = PlaylistRepoImpl()
) : ViewModel() {

    val allSongs: MutableList<Song> = repository.getAllSongs()

    val playlists: List<PlaylistModel> =
        repository.getPlaylists(allSongs)

    fun onFavoriteClick(songId: Int) {
        repository.addOrRemoveFavorite(songId)
    }

    fun onDownloadClick(songId: Int) {
        repository.addOrRemoveDownload(songId)
    }
}

