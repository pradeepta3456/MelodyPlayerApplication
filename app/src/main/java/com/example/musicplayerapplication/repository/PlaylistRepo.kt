package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.PlaylistModel
import com.example.musicplayerapplication.model.Song


interface PlaylistRepository {

    fun getAllSongs(): MutableList<Song>

    fun getPlaylists(allSongs: MutableList<Song>): List<PlaylistModel>

    fun addOrRemoveFavorite(songId: Int)

    fun addOrRemoveDownload(songId: Int)
}

