package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song

class HomeRepoImpl : HomeRepo {

    // Sample data - Replace with actual database/API calls
    // Using Firebase Storage URLs for images
    private val sampleSongs = mutableListOf(
        Song(1, "Starlight", "The Luminaries", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fstarlight.jpg?alt=media", 271, "Neon Dreams", "3:45", isFavorite = false),
        Song(2, "Moonlight Sonata", "Beethoven", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fmoonlight.jpg?alt=media", 180, "Classical Hits", "4:20"),
        Song(3, "Sunset Drive", "Synthwave", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fsunset.jpg?alt=media", 95, "Retro Wave", "3:15"),
        Song(4, "Eclipse", "Aurora", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Feclipse.jpg?alt=media", 120, "Northern Lights", "4:10"),
        Song(5, "Ocean Waves", "Chill Masters", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Focean.jpg?alt=media", 200, "Peaceful", "5:30")
    )

    private val sampleAlbums = listOf(
        Album(1, "Night Vibes", "Chill", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Fnight_vibes.jpg?alt=media", 15, 2023),
        Album(2, "Fire Beats", "Hip Hop", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Ffire_beats.jpg?alt=media", 12, 2024),
        Album(3, "Synthwave Dreams", "Electronic", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Fsynthwave.jpg?alt=media", 10, 2023),
        Album(4, "Acoustic Moods", "Acoustic", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Facoustic.jpg?alt=media", 8, 2024)
    )

    override fun getRecentSongs(): List<Song> = sampleSongs

    override fun getTrendingAlbums(): List<Album> = sampleAlbums

    override fun toggleFavorite(songId: Int) {
        val song = sampleSongs.find { it.id == songId }
        song?.let {
            it.isFavorite = !it.isFavorite
        }
    }

    override fun playSong(songId: Int) {
        // Play song logic - integrate with ExoPlayer
        val song = sampleSongs.find { it.id == songId }
        // Start playback
    }

    override fun refreshData() {
        // Refresh data from API/Database
    }
}

