package com.example.musicplayerapplication.repository

import android.content.Context
import android.net.Uri
import com.example.musicplayerapplication.Utils.CloudinaryHelper
import com.example.musicplayerapplication.model.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MusicRepoImpl(private val context: Context) : MusicRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val songsRef = database.child("songs")
    private val userFavoritesRef = database.child("user_favorites")
    private val recentlyPlayedRef = database.child("recently_played")

    override suspend fun uploadSong(
        audioUri: Uri,
        coverUri: Uri?,
        songDetails: Song,
        onProgress: (Float) -> Unit
    ): Result<Song> {
        return try {
            val songId = UUID.randomUUID().toString()
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            // Validate Cloudinary initialization
            if (!CloudinaryHelper.isInitialized()) {
                return Result.failure(Exception("Cloudinary not initialized. Please configure Cloudinary in MainActivity."))
            }

            // Upload audio file to Cloudinary
            onProgress(0.1f)
            val audioFileName = "${songDetails.title}_${songId}".replace(" ", "_")
            val audioUrl = CloudinaryHelper.uploadAudio(
                context = context,
                audioUri = audioUri,
                fileName = audioFileName,
                onProgress = { progress ->
                    // Map progress to 0.1 to 0.7 range (audio takes most time)
                    onProgress(0.1f + (progress * 0.6f))
                }
            )

            // Upload cover image if provided
            var coverUrl = songDetails.coverUrl
            if (coverUri != null) {
                onProgress(0.7f)
                val coverFileName = "${songDetails.title}_cover_${songId}".replace(" ", "_")
                coverUrl = CloudinaryHelper.uploadImage(
                    context = context,
                    imageUri = coverUri,
                    fileName = coverFileName,
                    onProgress = { progress ->
                        // Map progress to 0.7 to 0.9 range
                        onProgress(0.7f + (progress * 0.2f))
                    }
                )
            }

            onProgress(0.9f)

            // Create song object with all details
            val newSong = songDetails.copy(
                id = songId,
                audioUrl = audioUrl,
                coverUrl = coverUrl,
                uploadedBy = userId,
                addedDate = System.currentTimeMillis(),
                modifiedDate = System.currentTimeMillis()
            )

            // Save to Firebase Realtime Database
            songsRef.child(songId).setValue(newSong).await()

            onProgress(1.0f)
            Result.success(newSong)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllSongs(): Result<List<Song>> {
        return try {
            val snapshot = songsRef.get().await()
            val songs = snapshot.children.mapNotNull { it.getValue(Song::class.java) }
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongById(songId: String): Result<Song> {
        return try {
            val snapshot = songsRef.child(songId).get().await()
            val song = snapshot.getValue(Song::class.java)
            if (song != null) {
                Result.success(song)
            } else {
                Result.failure(Exception("Song not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongsByArtist(artist: String): Result<List<Song>> {
        return try {
            val snapshot = songsRef.orderByChild("artist").equalTo(artist).get().await()
            val songs = snapshot.children.mapNotNull { it.getValue(Song::class.java) }
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongsByAlbum(album: String): Result<List<Song>> {
        return try {
            val snapshot = songsRef.orderByChild("album").equalTo(album).get().await()
            val songs = snapshot.children.mapNotNull { it.getValue(Song::class.java) }
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongsByGenre(genre: String): Result<List<Song>> {
        return try {
            val snapshot = songsRef.orderByChild("genre").equalTo(genre).get().await()
            val songs = snapshot.children.mapNotNull { it.getValue(Song::class.java) }
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserUploadedSongs(userId: String): Result<List<Song>> {
        return try {
            val snapshot = songsRef.orderByChild("uploadedBy").equalTo(userId).get().await()
            val songs = snapshot.children.mapNotNull { it.getValue(Song::class.java) }
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFavoriteSongs(userId: String): Result<List<Song>> {
        return try {
            val favoritesSnapshot = userFavoritesRef.child(userId).get().await()
            val songIds = favoritesSnapshot.children.mapNotNull { it.key }

            val songs = songIds.mapNotNull { songId ->
                songsRef.child(songId).get().await().getValue(Song::class.java)
            }

            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentlyPlayed(userId: String): Result<List<Song>> {
        return try {
            val snapshot = recentlyPlayedRef.child(userId)
                .orderByChild("timestamp")
                .limitToLast(20)
                .get()
                .await()

            val songIds = snapshot.children.mapNotNull {
                it.child("songId").getValue(String::class.java)
            }

            val songs = songIds.mapNotNull { songId ->
                songsRef.child(songId).get().await().getValue(Song::class.java)
            }

            Result.success(songs.reversed())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrendingSongs(limit: Int): Result<List<Song>> {
        return try {
            val snapshot = songsRef.orderByChild("plays").limitToLast(limit).get().await()
            val songs = snapshot.children.mapNotNull { it.getValue(Song::class.java) }
            Result.success(songs.reversed())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSong(songId: String, updatedSong: Song): Result<Boolean> {
        return try {
            val updates = updatedSong.copy(modifiedDate = System.currentTimeMillis())
            songsRef.child(songId).setValue(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun incrementPlayCount(songId: String): Result<Boolean> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            // Increment play count
            songsRef.child(songId).child("plays").runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentPlays = currentData.getValue(Int::class.java) ?: 0
                    currentData.value = currentPlays + 1
                    return Transaction.success(currentData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {}
            })

            // Add to recently played
            val playData = mapOf(
                "songId" to songId,
                "timestamp" to System.currentTimeMillis()
            )
            recentlyPlayedRef.child(userId).push().setValue(playData).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(songId: String, userId: String, isFavorite: Boolean): Result<Boolean> {
        return try {
            if (isFavorite) {
                userFavoritesRef.child(userId).child(songId).setValue(true).await()
            } else {
                userFavoritesRef.child(userId).child(songId).removeValue().await()
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLikes(songId: String, increment: Boolean): Result<Boolean> {
        return try {
            songsRef.child(songId).child("likes").runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentLikes = currentData.getValue(Int::class.java) ?: 0
                    currentData.value = if (increment) currentLikes + 1 else maxOf(0, currentLikes - 1)
                    return Transaction.success(currentData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {}
            })
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSong(songId: String, userId: String): Result<Boolean> {
        return try {
            // First verify the song exists and user has permission to delete
            val songSnapshot = songsRef.child(songId).get().await()
            val song = songSnapshot.getValue(Song::class.java)

            if (song == null) {
                return Result.failure(Exception("Song not found"))
            }

            if (song.uploadedBy != userId) {
                return Result.failure(Exception("You don't have permission to delete this song"))
            }

            // Note: Cloudinary files can be deleted via API if needed
            // For now, we only delete from database
            // To delete from Cloudinary, you'd need to implement the Admin API

            // Delete from database
            songsRef.child(songId).removeValue().await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchSongs(query: String): Result<List<Song>> {
        return try {
            val allSongs = songsRef.get().await()
            val songs = allSongs.children.mapNotNull { it.getValue(Song::class.java) }

            val filteredSongs = songs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                song.artist.contains(query, ignoreCase = true) ||
                song.album.contains(query, ignoreCase = true) ||
                song.genre.contains(query, ignoreCase = true)
            }

            Result.success(filteredSongs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToRecentlyPlayed(userId: String, song: Song): Result<Unit> {
        return try {
            val playData = mapOf(
                "songId" to song.id,
                "timestamp" to System.currentTimeMillis()
            )
            recentlyPlayedRef.child(userId).push().setValue(playData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToFavorites(userId: String, song: Song): Result<Unit> {
        return try {
            userFavoritesRef.child(userId).child(song.id).setValue(true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(userId: String, songId: String): Result<Unit> {
        return try {
            userFavoritesRef.child(userId).child(songId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
