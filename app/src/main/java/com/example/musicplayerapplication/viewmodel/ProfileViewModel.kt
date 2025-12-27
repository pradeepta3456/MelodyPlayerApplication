package com.example.musicplayerapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Artist
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.ProfileRepo
import com.example.musicplayerapplication.repository.ProfileRepoImpl

class ProfileViewModel(
    private val repository: ProfileRepo = ProfileRepoImpl()
) : ViewModel() {

    val topSongs: List<Song> = repository.getTopSongs()

    val topArtists: List<Artist> = repository.getTopArtists()

    val achievements: List<Achievement> = repository.getAchievements()
}