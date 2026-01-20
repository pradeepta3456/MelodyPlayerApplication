package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song

interface HomeRepo {
    fun getRecentSongs(): List<Song>
    fun getTrendingAlbums(): List<Album>
    fun toggleFavorite(songId: Int)
    fun playSong(songId: Int)
    fun refreshData()
}
