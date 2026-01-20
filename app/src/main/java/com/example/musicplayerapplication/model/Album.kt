package com.example.musicplayerapplication.model

data class Album(
    val id: Int = 0,
    val title: String,
    val artistVibes: String, // Artist name or vibe description
    val imageUrl: String = "", // Firebase Storage URL
    val songCount: Int = 0,
    val releaseYear: Int = 0
)
