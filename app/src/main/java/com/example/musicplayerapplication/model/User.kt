package com.example.musicplayerapplication.model

/**
 * Core user profile stored in Firebase Realtime Database under `/users/{uid}`.
 *
 * A single user can also act as an artist. We keep this simple via the `isArtist` flag,
 * and later we can extend this with more fine-grained roles if needed.
 */
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val listeningTime: Long = 0, // in milliseconds
    val songsPlayed: Int = 0,
    val topGenre: String = "",
    val dayStreak: Int = 0,
    val isArtist: Boolean = true, // same account can upload songs as an artist
    val createdAt: Long = System.currentTimeMillis()
)
