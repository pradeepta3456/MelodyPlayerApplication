package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song

class HomeRepoImpl : HomeRepo {

    // Sample data - Using empty URLs for placeholders
    // Upload actual music files via AddMusicActivity to see real data
    private val sampleSongs = mutableListOf(
        Song(id = "1", title = "Starlight", artist = "The Luminaries", coverUrl = "", plays = 271, album = "Neon Dreams", durationFormatted = "3:45", isFavorite = false, audioUrl = ""),
        Song(id = "2", title = "Moonlight Sonata", artist = "Beethoven", coverUrl = "", plays = 180, album = "Classical Hits", durationFormatted = "4:20", audioUrl = ""),
        Song(id = "3", title = "Sunset Drive", artist = "Synthwave", coverUrl = "", plays = 95, album = "Retro Wave", durationFormatted = "3:15", audioUrl = ""),
        Song(id = "4", title = "Eclipse", artist = "Aurora", coverUrl = "", plays = 120, album = "Northern Lights", durationFormatted = "4:10", audioUrl = ""),
        Song(id = "5", title = "Ocean Waves", artist = "Chill Masters", coverUrl = "", plays = 200, album = "Peaceful", durationFormatted = "5:30", audioUrl = "")
    )

    private val sampleAlbums = listOf(
        Album(1, "Night Vibes", "Chill", "", "", 15, 2023),
        Album(2, "Fire Beats", "Hip Hop", "", "", 12, 2024),
        Album(3, "Synthwave Dreams", "Electronic", "", "", 10, 2023),
        Album(4, "Acoustic Moods", "Acoustic", "", "", 8, 2024)
    )

    override fun getRecentSongs(): List<Song> = sampleSongs

    override fun getTrendingAlbums(): List<Album> = sampleAlbums

    override fun toggleFavorite(songId: String) {
        val song = sampleSongs.find { it.id == songId }
        song?.let {
            it.isFavorite = !it.isFavorite
        }
    }

    override fun playSong(songId: String) {
        // Play song logic - integrate with ExoPlayer
        val song = sampleSongs.find { it.id == songId }
        // Start playback
    }

    override fun refreshData() {
        // Refresh data from API/Database
    }
}

