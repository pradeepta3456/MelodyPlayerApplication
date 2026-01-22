package com.example.musicplayerapplication.model

data class Album(
    val id: Int = 0,
    val title: String,
    val artistVibes: String, // Artist name or vibe description
    val coverUrl: String = "", // Firebase Storage URL
    val imageUrl: String = "", // Deprecated: Use coverUrl instead
    val songCount: Int = 0,
    val releaseYear: Int = 0
)
