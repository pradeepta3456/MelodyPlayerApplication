package com.example.musicplayerapplication.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.viewmodel.PlaylistModel

class PlaylistRepoImpl : PlaylistRepository {

    private val allSongs = mutableStateListOf(
        Song(1, "Kiss Me", "Red Love", R.drawable.kissme),
        Song(2, "Radio", "Lana Del Rey", R.drawable.lana),
        Song(3, "Face", "Larosea", R.drawable.larosea),
        Song(4, "Sunset Dreams", "Ambient Collective", R.drawable.baseline_library_music_24),
        Song(5, "Midnight Coffee", "Jazz Essentials", R.drawable.baseline_library_music_24)
    )


    private val playlists = mutableListOf<PlaylistModel>()

    override fun getAllSongs(): MutableList<Song> = allSongs

    override fun getPlaylists(allSongs: MutableList<Song>): List<PlaylistModel> {
        if (playlists.isEmpty()) {
            playlists.addAll(
                listOf(
                    PlaylistModel(
                        id = 1,
                        name = "Chill Vibes",
                        description = "Your perfect relaxation mix",
                        icon = Icons.Default.LibraryMusic,
                        gradient = Brush.verticalGradient(listOf(Color(0xFF6AD0A6), Color(0xFF043454))),
                        songs = allSongs.toMutableStateList()
                    ),
                    PlaylistModel(
                        id = 2,
                        name = "Favorite Songs",
                        description = "Your most loved tracks",
                        icon = Icons.Default.Favorite,
                        gradient = Brush.verticalGradient(listOf(Color(0xFFEC4899), Color(0xFFF43F5E))),
                        songs = mutableStateListOf()
                    ),
                    PlaylistModel(
                        id = 3,
                        name = "Downloaded",
                        description = "Available offline",
                        icon = Icons.Default.Download,
                        gradient = Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))),
                        songs = mutableStateListOf()
                    )
                )
            )
        }
        return playlists
    }

    override fun addOrRemoveFavorite(songId: Int) {
        val song = allSongs.find { it.id == songId } ?: return
        val favPlaylist = playlists.first { it.name == "Favorite Songs" }

        song.isFavorite = !song.isFavorite
        if (song.isFavorite) {
            if (favPlaylist.songs.none { it.id == song.id }) favPlaylist.songs.add(song)
        } else {
            favPlaylist.songs.removeAll { it.id == song.id }
        }
    }

    override fun addOrRemoveDownload(songId: Int) {
        val song = allSongs.find { it.id == songId } ?: return
        val downloadPlaylist = playlists.first { it.name == "Downloaded" }

        song.isDownloaded = !song.isDownloaded
        if (song.isDownloaded) {
            if (downloadPlaylist.songs.none { it.id == song.id }) downloadPlaylist.songs.add(song)
        } else {
            downloadPlaylist.songs.removeAll { it.id == song.id }
        }
    }
}
