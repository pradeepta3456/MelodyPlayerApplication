package com.example.musicplayerapplication.model

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val cover: Int, // Drawable resource ID
    val plays: Int = 0,
    val album: String = "",
    val duration: String = "0:00",
    val coverResId: Int? = null,
    var isFavorite: Boolean = false,
    var isDownloaded: Boolean = false,
    val filePath: String = "",
    val addedDate: Long = System.currentTimeMillis()
)