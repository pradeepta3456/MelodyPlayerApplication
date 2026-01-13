package com.example.musicplayerapplication.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val cover: Int = 0,  // For drawable resources (old mock data)
    val coverUri: Uri? = null,  // For real album art from device
    val plays: Int = 0,
    val duration: Long = 0,  // in milliseconds
    val uri: Uri? = null,  // Content URI to play the song
    val path: String = "",  // File path
    val albumId: Long = 0
)
