package com.example.musicplayerapplication.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.musicplayerapplication.model.Song

data class PlaylistModel(
    val id: Int,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val gradient: Brush,
    val songs: MutableList<Song> = mutableStateListOf()
)



