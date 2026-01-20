package com.example.musicplayerapplication.model
data class MusicGenre(
    val id: Int = 0,
    val name: String,
    val displayName: String,
    val color: Long,
    val songCount: Int = 0
)
