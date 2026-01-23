package com.example.musicplayerapplication.model

/**
 * User profile data stored in Firebase
 * Path: /users/{userId}/profile
 */
data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "Music Lover",
    val profileImageUrl: String = "",
    val bio: String = "",
    val memberSince: Long = System.currentTimeMillis(),
    val isArtist: Boolean = false
)

/**
 * User statistics stored in Firebase
 * Path: /users/{userId}/stats
 */
data class UserStats(
    val totalListeningTime: Long = 0, // in milliseconds
    val songsPlayed: Int = 0,
    val topGenre: String = "Unknown",
    val dayStreak: Int = 0,
    val lastActiveDate: String = "", // Format: yyyy-MM-dd
    val favoritesCount: Int = 0,
    val playlistsCount: Int = 0
)

/**
 * User listening history entry
 * Path: /users/{userId}/listeningHistory/{timestamp}
 */
data class ListeningHistoryEntry(
    val songId: String = "",
    val songTitle: String = "",
    val artist: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val durationPlayed: Long = 0 // in milliseconds
)

/**
 * Top song with play count
 * Calculated from listening history
 */
data class TopSong(
    val songId: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val coverUrl: String = "",
    val playCount: Int = 0
)

/**
 * Top artist with play count
 * Calculated from listening history
 */
data class TopArtist(
    val artistName: String = "",
    val playCount: Int = 0,
    val songCount: Int = 0
)

/**
 * Weekly listening pattern
 * Days: 0 = Sunday, 1 = Monday, ..., 6 = Saturday
 */
data class WeeklyPattern(
    val dayOfWeek: Int = 0,
    val listeningTime: Long = 0, // in minutes
    val songsPlayed: Int = 0
)
