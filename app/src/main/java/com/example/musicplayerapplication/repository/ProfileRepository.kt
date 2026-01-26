package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.*

/**
 * Repository interface for user profile and statistics
 * Follows MVVM architecture with Firebase Realtime Database
 */
interface ProfileRepository {

    /**
     * Get user profile
     */
    suspend fun getUserProfile(userId: String): UserProfile?

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(userId: String, profile: UserProfile): Boolean

    /**
     * Get user statistics
     */
    suspend fun getUserStats(userId: String): UserStats?

    /**
     * Update user statistics
     */
    suspend fun updateUserStats(userId: String, stats: UserStats): Boolean

    /**
     * Get top songs (most played)
     */
    suspend fun getTopSongs(userId: String, limit: Int = 10): List<TopSong>

    /**
     * Get top artists (most played)
     */
    suspend fun getTopArtists(userId: String, limit: Int = 4): List<TopArtist>

    /**
     * Get achievements
     */
    suspend fun getAchievements(userId: String): List<Achievement>

    /**
     * Get weekly listening pattern
     */
    suspend fun getWeeklyPattern(userId: String): List<WeeklyPattern>

    /**
     * Track song play for statistics
     */
    suspend fun trackSongPlay(
        userId: String,
        songId: String,
        songTitle: String,
        artist: String,
        durationPlayed: Long
    ): Boolean

    /**
     * Initialize user profile and stats (called on first login)
     */
    suspend fun initializeUserData(userId: String, email: String, displayName: String): Boolean
}
