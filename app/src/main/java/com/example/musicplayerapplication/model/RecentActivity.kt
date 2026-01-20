package com.example.musicplayerapplication.model

data class RecentActivity(
    val id: Int = 0,
    val title: String,
    val imageUrl: String = "", // Firebase Storage URL
    val timestamp: Long = System.currentTimeMillis(),
    val type: ActivityType = ActivityType.SONG_PLAYED
)

enum class ActivityType {
    SONG_PLAYED,
    PLAYLIST_CREATED,
    SONG_LIKED,
    ALBUM_ADDED
}
