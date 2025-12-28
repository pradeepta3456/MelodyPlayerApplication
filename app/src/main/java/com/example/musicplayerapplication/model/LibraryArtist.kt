package com.example.musicplayerapplication.model



data class LibraryArtist(
    val name: String,
    val songCount: Int = 0,
    val albumCount: Int = 0,
    val imageResId: Int? = null
)
