package com.example.melodyplay.viewmodel


import androidx.lifecycle.ViewModel
import com.example.melodyplay.repository.ProfileRepo
import com.example.melodyplay.repositoryimpl.ProfileRepoImpl

class ProfileViewModel(
    private val repo: ProfileRepo = ProfileRepoImpl()
) : ViewModel() {

    val topSongs = repo.getTopSongs()
    val topArtists = repo.getTopArtists()
    val achievements = repo.getAchievements()
}
