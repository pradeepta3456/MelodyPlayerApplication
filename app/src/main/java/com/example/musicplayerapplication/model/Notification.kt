package com.example.musicplayerapplication.model

/**
 * Notification data model
 * Represents a notification in the app
 *
 * Firebase Structure:
 * /notifications/{notificationId}
 */
data class Notification(
    val id: String = "",
    val type: NotificationType = NotificationType.SONG_ADDED,
    val title: String = "",
    val message: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderEmail: String = "",
    val songId: String? = null,
    val songTitle: String? = null,
    val songArtist: String? = null,
    val songCoverUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

/**
 * Notification types
 */
enum class NotificationType {
    SONG_ADDED,      // When a user adds a new song
    SONG_LIKED,      // When someone likes your song
    PLAYLIST_SHARED, // When someone shares a playlist
    ACHIEVEMENT,     // Achievement unlocked
    SYSTEM,          // System notifications
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

/**
 * User-specific notification data
 * Firebase Structure:
 * /userNotifications/{userId}/{notificationId}
 */
data class UserNotification(
    val notificationId: String = "",
    val isRead: Boolean = false,
    val receivedAt: Long = System.currentTimeMillis()
)

