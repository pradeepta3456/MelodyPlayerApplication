package com.example.musicplayerapplication.view

import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.HomeRepo
import com.example.musicplayerapplication.repository.HomeRepoImpl

class HomeViewModel(
    private val repository: HomeRepo = HomeRepoImpl()
) : ViewModel() {
    val recentSongs: List<Song> = repository.getRecentSongs()
    val trendingAlbums: List<Album> = repository.getTrendingAlbums()
}

