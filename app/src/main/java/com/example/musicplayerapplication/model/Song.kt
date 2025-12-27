package com.example.musicplayerapplication.model
data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val cover: Int,
    val plays: Int = 0,
    var isFavorite: Boolean = false,
    var isDownloaded: Boolean = false
)



