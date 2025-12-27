package com.example.musicplayerapplication.model

import androidx.compose.ui.graphics.Brush
import com.example.musicplayerapplication.Song


data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val gradient: Brush,
    val songs: MutableList<Song>
)





