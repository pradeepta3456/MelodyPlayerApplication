package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Song
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementation of SavedRepository
 * Manages favorite songs using Firebase Realtime Database
 */
class SavedRepoImpl : SavedRepository {

    private val database = FirebaseDatabase.getInstance()
    private val favoritesRef = database.getReference("favorites")
    private val songsRef = database.getReference("songs")

    override fun getSavedSongsFlow(): Flow<List<Song>> = callbackFlow {
        // This would need userId - for now returning empty flow
        // Will be properly implemented with userId in ViewModel
        trySend(emptyList())
        awaitClose {}
    }

    override suspend fun getSavedSongs(userId: String): List<Song> {
        return try {
            // Get favorite song IDs for this user
            val favSnapshot = favoritesRef.child(userId).get().await()
            val favoriteSongIds = mutableListOf<String>()

            favSnapshot.children.forEach { child ->
                child.key?.let { favoriteSongIds.add(it) }
            }

            if (favoriteSongIds.isEmpty()) {
                return emptyList()
            }

            // Fetch all songs
            val songsSnapshot = songsRef.get().await()
            val allSongs = songsSnapshot.children.mapNotNull {
                it.getValue(Song::class.java)?.copy(id = it.key ?: "")
            }

            // Filter and mark favorites
            allSongs.filter { song ->
                favoriteSongIds.contains(song.id)
            }.map { song ->
                song.copy(isFavorite = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun addToFavorites(userId: String, song: Song): Boolean {
        return try {
            // Store song ID in user's favorites with timestamp
            val favoriteData = mapOf(
                "addedAt" to System.currentTimeMillis(),
                "songId" to song.id,
                "title" to song.title,
                "artist" to song.artist
            )
            favoritesRef.child(userId).child(song.id).setValue(favoriteData).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun removeFromFavorites(userId: String, songId: String): Boolean {
        return try {
            favoritesRef.child(userId).child(songId).removeValue().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun isFavorite(userId: String, songId: String): Boolean {
        return try {
            val snapshot = favoritesRef.child(userId).child(songId).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getFavoritesCount(userId: String): Int {
        return try {
            val snapshot = favoritesRef.child(userId).get().await()
            snapshot.childrenCount.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    override suspend fun clearAllFavorites(userId: String): Boolean {
        return try {
            favoritesRef.child(userId).removeValue().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
