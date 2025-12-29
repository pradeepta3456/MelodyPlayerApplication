package com.example.musicplayerapplication.viewmodel

import androidx.compose.ui.graphics.Brush

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val gradient: Brush,
    val songs: MutableList<Song>
)



