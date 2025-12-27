package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Artist
import com.example.musicplayerapplication.model.Song

interface ProfileRepo {

    fun getTopSongs(): List<Song>

    fun getTopArtists(): List<Artist>

    fun getAchievements(): List<Achievement>
}