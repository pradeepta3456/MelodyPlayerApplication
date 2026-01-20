package com.example.musicplayerapplication.repository

import Artist
import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.model.User

interface ProfileRepo {
    fun getTopSongs(): List<Song>
    fun getTopArtists(): List<Artist>
    fun getAchievements(): List<Achievement>
    fun getUserProfile(): User
    fun updateProfile(displayName: String, profileImageUrl: String)
    fun getUserStats(): Map<String, Any>
}
