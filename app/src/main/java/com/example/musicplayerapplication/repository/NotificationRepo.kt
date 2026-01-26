package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Notification

/**
 * Notification Repository Interface
 * Handles all notification-related data operations
 */
interface NotificationRepository {
    /**
     * Create a notification for new song upload
     * Sends to all users except the uploader
     */
    suspend fun createSongAddedNotification(
        senderId: String,
        senderName: String,
        senderEmail: String,
        songId: String,
        songTitle: String,
        songArtist: String,
        songCoverUrl: String
    ): Result<String>

    /**
     * Get notifications for a specific user
     */
    suspend fun getUserNotifications(userId: String, limit: Int = 50): Result<List<Notification>>

    /**
     * Mark notification as read for a user
     */
    suspend fun markAsRead(userId: String, notificationId: String): Result<Unit>

    /**
     * Mark all notifications as read for a user
     */
    suspend fun markAllAsRead(userId: String): Result<Unit>

    /**
     * Delete notification for a user
     */
    suspend fun deleteNotification(userId: String, notificationId: String): Result<Unit>

    /**
     * Clear all notifications for a user
     */
    suspend fun clearAllNotifications(userId: String): Result<Unit>

    /**
     * Get unread notification count
     */
    suspend fun getUnreadCount(userId: String): Result<Int>
}

