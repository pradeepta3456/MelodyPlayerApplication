package com.example.musicplayerapplication.model

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val coverResId: Int? = null,
    val plays: Int = 0,
    val duration: String = "0:00",
    var isFavorite: Boolean = false,
    var isDownloaded: Boolean = false
)



