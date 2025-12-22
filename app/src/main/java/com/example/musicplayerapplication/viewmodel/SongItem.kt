package com.example.musicplayerapplication.viewmodel

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val cover: Int,
    var isFavorite: Boolean = false,
    var isDownloaded: Boolean = false
)

