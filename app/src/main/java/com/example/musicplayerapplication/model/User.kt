package com.example.musicplayerapplication.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val listeningTime: Long = 0, // in milliseconds
    val songsPlayed: Int = 0,
    val topGenre: String = "",
    val dayStreak: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
