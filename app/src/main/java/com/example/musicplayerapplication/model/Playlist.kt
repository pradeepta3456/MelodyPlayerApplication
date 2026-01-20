package com.example.musicplayerapplication.model

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val coverImageUrl: String = "",
    val songCount: Int = 0,
    val isAiGenerated: Boolean = false,
    val songs: List<Song> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
