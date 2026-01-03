package com.example.melodyplay.repository

import com.example.melodyplay.model.*

interface ProfileRepo {

    fun getTopSongs(): List<Song>

    fun getTopArtists(): List<Artist>

    fun getAchievements(): List<Achievement>
}
