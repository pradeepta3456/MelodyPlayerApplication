package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing saved/favorite songs
 * Follows MVVM architecture pattern
 */
interface SavedRepository {

    /**
     * Get all saved songs as Flow for reactive updates
     */
    fun getSavedSongsFlow(): Flow<List<Song>>

    /**
     * Get all saved songs (one-time fetch)
     */
    suspend fun getSavedSongs(userId: String): List<Song>

    /**
     * Add a song to favorites
     */
    suspend fun addToFavorites(userId: String, song: Song): Boolean

    /**
     * Remove a song from favorites
     */
    suspend fun removeFromFavorites(userId: String, songId: String): Boolean

    /**
     * Check if a song is in favorites
     */
    suspend fun isFavorite(userId: String, songId: String): Boolean

    /**
     * Get favorite songs count
     */
    suspend fun getFavoritesCount(userId: String): Int

    /**
     * Clear all favorites for a user
     */
    suspend fun clearAllFavorites(userId: String): Boolean
}
