package com.example.musicplayerapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.repository.ProfileRepo
import com.example.musicplayerapplication.repository.ProfileRepoImpl

data class ProfileViewModel( private val repo: ProfileRepo = ProfileRepoImpl()
) : ViewModel() {

    val topSongs = repo.getTopSongs()
    val topArtists = repo.getTopArtists()
    val achievements = repo.getAchievements()
}
