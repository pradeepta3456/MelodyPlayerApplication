package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Playlist
import com.example.musicplayerapplication.model.Song

class PlaylistRepoImpl : PlaylistRepository {

    private val playlists = mutableListOf(
        Playlist(
            id = 1,
            name = "Focus Flow",
            description = "AI curated for productivity",
            songCount = 32,
            isAiGenerated = true
        ),
        Playlist(
            id = 2,
            name = "Evening Calm",
            description = "Relaxing evening vibes",
            songCount = 15,
            isAiGenerated = true
        ),
        Playlist(
            id = 3,
            name = "Chill Vibes",
            description = "Relaxing tunes for any time",
            songCount = 24,
            isAiGenerated = false
        ),
        Playlist(
            id = 4,
            name = "Workout Energy",
            description = "High energy beats",
            songCount = 18,
            isAiGenerated = false
        )
    )

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

