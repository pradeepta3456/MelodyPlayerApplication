package com.example.musicplayerapplication.view

import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.PlaylistRepoImpl
import com.example.musicplayerapplication.repository.PlaylistRepository
import com.example.musicplayerapplication.viewmodel.PlaylistModel

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

