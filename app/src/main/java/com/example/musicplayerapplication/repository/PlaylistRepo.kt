package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Playlist
import com.example.musicplayerapplication.model.Song


interface PlaylistRepository {
    fun getAllPlaylists(): List<Playlist>
    fun getPlaylistById(id: Int): Playlist?
    fun createPlaylist(playlist: Playlist)
    fun updatePlaylist(playlist: Playlist)
    fun deletePlaylist(playlistId: Int)
    fun addSongToPlaylist(playlistId: Int, songId: Int)
    fun removeSongFromPlaylist(playlistId: Int, songId: Int)
    fun getPlaylistSongs(playlistId: Int): List<Song>
}

