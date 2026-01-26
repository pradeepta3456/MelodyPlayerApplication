package com.example.musicplayerapplication.model

data class Song(
    val id: String = "", // Firebase document ID
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val genre: String = "",
    val coverUrl: String = "", // Firebase Storage URL for cover image
    val audioUrl: String = "", // Firebase Storage URL for audio file
    val duration: Long = 0L, // Duration in milliseconds
    val durationFormatted: String = "0:00", // Formatted duration (e.g., "3:45")
    val fileSize: Long = 0L, // File size in bytes
    val bitrate: Int = 0, // Audio bitrate in kbps
    val year: Int = 0, // Release year
    val plays: Int = 0,
    val likes: Int = 0,
    var isFavorite: Boolean = false,
    var isDownloaded: Boolean = false,
    val uploadedBy: String = "", // User ID who uploaded the song
    val addedDate: Long = System.currentTimeMillis(),
    val modifiedDate: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList(), // Tags for categorization
    val lyrics: String = "", // Song lyrics (optional)
    val localFilePath: String = "" // Local file path when downloaded
) {
    companion object {
        fun formatDuration(millis: Long): String {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }

        fun formatFileSize(bytes: Long): String {
            return when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${bytes / 1024} KB"
                else -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
            }
        }
    }
}