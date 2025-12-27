package com.example.musicplayerapplication.viewmodel

import androidx.lifecycle.ViewModel

data class ProfileViewModel( private val repo: ProfileRepo = ProfileRepoImpl()
) : ViewModel() {

    val topSongs = repo.getTopSongs()
    val topArtists = repo.getTopArtists()
    val achievements = repo.getAchievements()
})
