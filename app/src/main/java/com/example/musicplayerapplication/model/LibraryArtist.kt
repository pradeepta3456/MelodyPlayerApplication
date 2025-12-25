package com.example.musicplayerapplication.model

data class LibraryArtist(
    val name: String,
    val songCount: Int = 0,
    val albumCount: Int = 0,
    val imageResId: Int? = null
)
data class Artist(val name: String, val imageRes: Int, val plays: Int)
data class Achievement(val title: String, val description: String, val iconRes: Int, val isCompleted: Boolean)
