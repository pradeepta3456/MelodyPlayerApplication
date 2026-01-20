package com.example.musicplayerapplication.model



data class LibraryArtist(
    val id: Int = 0,
    val name: String,
    val songCount: Int = 0,
    val albumCount: Int = 0,
    val imageUrl: String = "", // Firebase Storage URL
    val genre: String = ""
)
