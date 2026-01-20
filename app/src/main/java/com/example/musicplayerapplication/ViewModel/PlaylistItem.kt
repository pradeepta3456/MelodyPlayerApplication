package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.musicplayerapplication.model.Song

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val gradient: Brush,
    val songs: SnapshotStateList<Song> = mutableStateListOf()
)



