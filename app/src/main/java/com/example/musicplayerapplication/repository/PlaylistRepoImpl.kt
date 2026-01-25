package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Playlist
import com.example.musicplayerapplication.model.Song
import com.google.firebase.database.*
import com.example.musicplayerapplication.model.UserPlaylist
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class PlaylistRepoImpl : PlaylistRepository {

    // All playlists now generated from Firebase songs in PlaylistScreen
    private val playlists = mutableListOf<Playlist>()
    private val playlistSongs = mutableMapOf<Int, MutableList<Song>>()

    // Firebase properties
    private val database = FirebaseDatabase.getInstance()
    private val userPlaylistsRef = database.getReference("user_playlists")

    override fun getAllPlaylists(): List<Playlist> = playlists

    override fun getPlaylistById(id: Int): Playlist? {
        return playlists.find { it.id == id }
    }

    override fun createPlaylist(playlist: Playlist) {
        playlists.add(playlist)
    }

    override fun updatePlaylist(playlist: Playlist) {
        val index = playlists.indexOfFirst { it.id == playlist.id }
        if (index != -1) {
            playlists[index] = playlist
        }
    }

    override fun deletePlaylist(playlistId: Int) {
        playlists.removeIf { it.id == playlistId }
        playlistSongs.remove(playlistId)
    }

    override fun addSongToPlaylist(playlistId: Int, songId: Int) {
        // Add song to playlist
        if (!playlistSongs.containsKey(playlistId)) {
            playlistSongs[playlistId] = mutableListOf()
        }
        // Fetch song and add to list
    }

    override fun removeSongFromPlaylist(playlistId: Int, songId: Int) {
        playlistSongs[playlistId]?.removeIf { it.id == songId.toString() }
    }

    override fun getPlaylistSongs(playlistId: Int): List<Song> {
        return playlistSongs[playlistId] ?: emptyList()
    }

    // Firebase-backed user playlist methods

    suspend fun createUserPlaylist(userId: String, name: String, description: String, songIds: List<String> = emptyList()): Result<UserPlaylist> {
        return try {
            val playlistId = userPlaylistsRef.child(userId).push().key ?: return Result.failure(Exception("Failed to generate playlist ID"))
            val playlist = UserPlaylist(
                id = playlistId,
                userId = userId,
                name = name,
                description = description,
                songIds = songIds,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            userPlaylistsRef.child(userId).child(playlistId).setValue(playlist.toMap()).await()
            Result.success(playlist)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserPlaylists(userId: String): Result<List<UserPlaylist>> {
        return try {
            val snapshot = userPlaylistsRef.child(userId).get().await()
            val playlists = mutableListOf<UserPlaylist>()

            snapshot.children.forEach { child ->
                val playlistMap = child.value as? Map<String, Any> ?: return@forEach
                playlists.add(UserPlaylist.fromMap(child.key ?: "", playlistMap))
            }

            Result.success(playlists.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSongToUserPlaylist(userId: String, playlistId: String, songId: String): Result<Boolean> {
        return try {
            val playlistRef = userPlaylistsRef.child(userId).child(playlistId)
            val snapshot = playlistRef.get().await()
            val playlistMap = snapshot.value as? Map<String, Any> ?: return Result.failure(Exception("Playlist not found"))

            val currentSongIds = (playlistMap["songIds"] as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf()

            if (!currentSongIds.contains(songId)) {
                currentSongIds.add(songId)
                playlistRef.child("songIds").setValue(currentSongIds).await()
                playlistRef.child("updatedAt").setValue(System.currentTimeMillis()).await()
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeSongFromUserPlaylist(userId: String, playlistId: String, songId: String): Result<Boolean> {
        return try {
            val playlistRef = userPlaylistsRef.child(userId).child(playlistId)
            val snapshot = playlistRef.get().await()
            val playlistMap = snapshot.value as? Map<String, Any> ?: return Result.failure(Exception("Playlist not found"))

            val currentSongIds = (playlistMap["songIds"] as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf()

            currentSongIds.remove(songId)
            playlistRef.child("songIds").setValue(currentSongIds).await()
            playlistRef.child("updatedAt").setValue(System.currentTimeMillis()).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUserPlaylist(userId: String, playlistId: String): Result<Boolean> {
        return try {
            userPlaylistsRef.child(userId).child(playlistId).removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

