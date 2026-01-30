package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song

class HomeRepoImpl : HomeRepo {

    // All data now comes from Firebase via MusicViewModel
    // This repository is no longer used for data storage

    override fun getRecentSongs(): List<Song> = emptyList()

    override fun getTrendingAlbums(): List<Album> = emptyList()

    override fun toggleFavorite(songId: String) {
        // Handled by MusicViewModel
    }

    override fun playSong(songId: String) {
        // Handled by MusicViewModel
    }

    override fun refreshData() {
        // Handled by MusicViewModel
    }
}

