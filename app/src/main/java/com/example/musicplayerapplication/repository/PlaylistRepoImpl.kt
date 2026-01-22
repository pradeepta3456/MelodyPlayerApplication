package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Playlist
import com.example.musicplayerapplication.model.Song

class PlaylistRepoImpl : PlaylistRepository {

    // All playlists now generated from Firebase songs in PlaylistScreen
    private val playlists = mutableListOf<Playlist>()
    private val playlistSongs = mutableMapOf<Int, MutableList<Song>>()

    override fun getAllPlaylists(): List<Playlist> = playlists

    override fun getPlaylistById(id: Int): Playlist? {
        return playlists.find { it.id == id }
    }

    override fun createPlaylist(playlist: Playlist) {
        playlists.add(playlist)
    }

    override fun updatePlaylist(playlist: Playlist) {
        val index = playlists.indexOfFirst { it.id == playlist.id }
        if (index != -1) {
            playlists[index] = playlist
        }
    }

    override fun deletePlaylist(playlistId: Int) {
        playlists.removeIf { it.id == playlistId }
        playlistSongs.remove(playlistId)
    }

    override fun addSongToPlaylist(playlistId: Int, songId: Int) {
        // Add song to playlist
        if (!playlistSongs.containsKey(playlistId)) {
            playlistSongs[playlistId] = mutableListOf()
        }
        // Fetch song and add to list
    }

    override fun removeSongFromPlaylist(playlistId: Int, songId: Int) {
        playlistSongs[playlistId]?.removeIf { it.id == songId.toString() }
    }

    override fun getPlaylistSongs(playlistId: Int): List<Song> {
        return playlistSongs[playlistId] ?: emptyList()
    }
}

