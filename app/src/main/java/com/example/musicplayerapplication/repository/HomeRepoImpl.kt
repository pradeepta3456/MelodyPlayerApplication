package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song

class HomeRepoImpl : HomeRepo {

    // Sample data - Replace with actual database/API calls
    private val sampleSongs = mutableListOf(
        Song(1, "Starlight", "The Luminaries", R.drawable.img_1, 271, "Neon Dreams", "3:45", isFavorite = false),
        Song(2, "Moonlight Sonata", "Beethoven", R.drawable.img_2, 180, "Classical Hits", "4:20"),
        Song(3, "Sunset Drive", "Synthwave", R.drawable.img_3, 95, "Retro Wave", "3:15"),
        Song(4, "Eclipse", "Aurora", R.drawable.img_4, 120, "Northern Lights", "4:10"),
        Song(5, "Ocean Waves", "Chill Masters", R.drawable.img_5, 200, "Peaceful", "5:30")
    )

    private val sampleAlbums = listOf(
        Album(1, "Night Vibes", "Chill", R.drawable.img_5, 15, 2023),
        Album(2, "Fire Beats", "Hip Hop", R.drawable.img_6, 12, 2024),
        Album(3, "Synthwave Dreams", "Electronic", R.drawable.img_7, 10, 2023),
        Album(4, "Acoustic Moods", "Acoustic", R.drawable.img_5, 8, 2024)
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

