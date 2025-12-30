package com.example.musicplayerapplication.repository

interface PlaylistRepository {

    fun getAllSongs(): MutableList<Song>

    fun getPlaylists(allSongs: MutableList<Song>): List<PlaylistModel>

    fun addOrRemoveFavorite(songId: Int)

    fun addOrRemoveDownload(songId: Int)
}
