package com.example.musicplayerapplication.repository

import android.net.Uri
import com.example.musicplayerapplication.model.Song

interface MusicRepository {
    // Create
    suspend fun uploadSong(
        audioUri: Uri,
        coverUri: Uri?,
        songDetails: Song,
        onProgress: (Float) -> Unit
    ): Result<Song>

    // Read
    suspend fun getAllSongs(): Result<List<Song>>
    suspend fun getSongById(songId: String): Result<Song>
    suspend fun getSongsByArtist(artist: String): Result<List<Song>>
    suspend fun getSongsByAlbum(album: String): Result<List<Song>>
    suspend fun getSongsByGenre(genre: String): Result<List<Song>>
    suspend fun getUserUploadedSongs(userId: String): Result<List<Song>>
    suspend fun getFavoriteSongs(userId: String): Result<List<Song>>
    suspend fun getRecentlyPlayed(userId: String): Result<List<Song>>
    suspend fun getTrendingSongs(limit: Int = 20): Result<List<Song>>

    // Update
    suspend fun updateSong(songId: String, updatedSong: Song): Result<Boolean>
    suspend fun incrementPlayCount(songId: String): Result<Boolean>
    suspend fun toggleFavorite(songId: String, userId: String, isFavorite: Boolean): Result<Boolean>
    suspend fun updateLikes(songId: String, increment: Boolean): Result<Boolean>

    // Delete
    suspend fun deleteSong(songId: String): Result<Boolean>

    // Search
    suspend fun searchSongs(query: String): Result<List<Song>>
}
