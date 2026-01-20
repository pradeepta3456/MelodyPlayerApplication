package com.example.musicplayerapplication.model

data class Notification(
    val id: Int = 0,
    val message: String,
    val hasImage: Boolean = false,
    val imageUrl: String = "", // Firebase Storage URL
    val textColor: Long = 0xFFB0B0B0,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.INFO
)

enum class NotificationType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR,
    ACHIEVEMENT
}

