package com.example.musicplayerapplication.model

data class UserStats(
    val listeningTime: String = "0h",
    val songsPlayed: Int = 0,
    val topGenre: String = "",
    val dayStreak: Int = 0
)
