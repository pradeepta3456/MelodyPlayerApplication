package com.example.musicplayerapplication.repository

import android.util.Log
import com.example.musicplayerapplication.model.Notification
import com.example.musicplayerapplication.model.NotificationType
import com.example.musicplayerapplication.model.UserNotification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Firebase Realtime Database Notification Repository Implementation
 *
 * Database Structure:
 * /notifications/{notificationId}        - Global notifications
 * /userNotifications/{userId}/{notificationId}  - User-specific notification status
 * /users/{userId}/profile                - User profile data
 */
class NotificationRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : NotificationRepository {

    private val notificationsRef = database.getReference("notifications")
    private val userNotificationsRef = database.getReference("userNotifications")
    private val usersRef = database.getReference("users")

    companion object {
        private const val TAG = "NotificationRepo"
    }

    /**
     * Create a notification when a new song is added
     * Sends to all users except the uploader
     */
    override suspend fun createSongAddedNotification(
        senderId: String,
        senderName: String,
        senderEmail: String,
        songId: String,
        songTitle: String,
        songArtist: String,
        songCoverUrl: String
    ): Result<String> {
        return try {
            Log.d(TAG, "Creating song added notification: $songTitle by $senderName")

            val notificationId = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()

            // Create notification object
            val notification = Notification(
                id = notificationId,
                type = NotificationType.SONG_ADDED,
                title = "New Song Added! 🎵",
                message = "$senderName added \"$songTitle\" by $songArtist",
                senderId = senderId,
                senderName = senderName,
                senderEmail = senderEmail,
                songId = songId,
                songTitle = songTitle,
                songArtist = songArtist,
                songCoverUrl = songCoverUrl,
                timestamp = timestamp,
                isRead = false
            )

            // Save global notification
            notificationsRef.child(notificationId).setValue(notification).await()
            Log.d(TAG, "Saved global notification: $notificationId")

            // Get all users - try to read from users node
            var allUserIds: List<String> = emptyList()
            try {
                val usersSnapshot = usersRef.get().await()
                allUserIds = usersSnapshot.children.mapNotNull { it.key }
                Log.d(TAG, "Found ${allUserIds.size} total users from /users")
            } catch (e: Exception) {
                Log.w(TAG, "Could not read /users, trying /userNotifications", e)
                // Fallback: try to get user IDs from userNotifications node
                try {
                    val userNotifsSnapshot = userNotificationsRef.get().await()
                    allUserIds = userNotifsSnapshot.children.mapNotNull { it.key }
                    Log.d(TAG, "Found ${allUserIds.size} users from /userNotifications")
                } catch (e2: Exception) {
                    Log.e(TAG, "Could not read users from any source", e2)
                }
            }

            // Send to all users except the sender
            val recipientIds = allUserIds.filter { it != senderId }
            Log.d(TAG, "Sending to ${recipientIds.size} recipients")

            // Create user-specific notification entries
            recipientIds.forEach { userId ->
                val userNotification = UserNotification(
                    notificationId = notificationId,
                    isRead = false,
                    receivedAt = timestamp
                )
                userNotificationsRef.child(userId).child(notificationId)
                    .setValue(userNotification)
                    .await()
            }

            Log.d(TAG, "Successfully created notification for ${recipientIds.size} users")
            Result.success(notificationId)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating song added notification", e)
            Result.failure(e)
        }
    }

    /**
     * Get notifications for a specific user
     */
    override suspend fun getUserNotifications(userId: String, limit: Int): Result<List<Notification>> {
        return try {
            Log.d(TAG, "Getting notifications for user: $userId")

            // Get user's notification IDs
            val userNotificationsSnapshot = userNotificationsRef.child(userId)
                .orderByChild("receivedAt")
                .limitToLast(limit)
                .get()
                .await()

            val notifications = mutableListOf<Notification>()

            // For each user notification, get the actual notification data
            for (userNotifSnap in userNotificationsSnapshot.children) {
                val userNotification = userNotifSnap.getValue(UserNotification::class.java)
                val notificationId = userNotifSnap.key

                if (userNotification != null && notificationId != null) {
                    // Get the actual notification
                    val notificationSnap = notificationsRef.child(notificationId).get().await()
                    val notification = notificationSnap.getValue(Notification::class.java)

                    if (notification != null) {
                        // Update read status from user notification
                        notifications.add(notification.copy(isRead = userNotification.isRead))
                    }
                }
            }

            // Sort by timestamp descending (newest first)
            val sortedNotifications = notifications.sortedByDescending { it.timestamp }
            Log.d(TAG, "Retrieved ${sortedNotifications.size} notifications")

            Result.success(sortedNotifications)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user notifications", e)
            Result.failure(e)
        }
    }

    /**
     * Mark notification as read for a user
     */
    override suspend fun markAsRead(userId: String, notificationId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Marking notification as read: $notificationId for user: $userId")

            userNotificationsRef.child(userId).child(notificationId)
                .child("isRead")
                .setValue(true)
                .await()

            Log.d(TAG, "Successfully marked as read")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notification as read", e)
            Result.failure(e)
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    override suspend fun markAllAsRead(userId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Marking all notifications as read for user: $userId")

            val userNotificationsSnapshot = userNotificationsRef.child(userId).get().await()

            val updates = mutableMapOf<String, Any>()
            userNotificationsSnapshot.children.forEach { snapshot ->
                snapshot.key?.let { notificationId ->
                    updates["$notificationId/isRead"] = true
                }
            }

            if (updates.isNotEmpty()) {
                userNotificationsRef.child(userId).updateChildren(updates).await()
                Log.d(TAG, "Marked ${updates.size} notifications as read")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking all as read", e)
            Result.failure(e)
        }
    }

    /**
     * Delete notification for a user
     */
    override suspend fun deleteNotification(userId: String, notificationId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting notification: $notificationId for user: $userId")

            userNotificationsRef.child(userId).child(notificationId).removeValue().await()

            Log.d(TAG, "Successfully deleted notification")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting notification", e)
            Result.failure(e)
        }
    }

    /**
     * Clear all notifications for a user
     */
    override suspend fun clearAllNotifications(userId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Clearing all notifications for user: $userId")

            userNotificationsRef.child(userId).removeValue().await()

            Log.d(TAG, "Successfully cleared all notifications")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all notifications", e)
            Result.failure(e)
        }
    }

    /**
     * Get unread notification count
     */
    override suspend fun getUnreadCount(userId: String): Result<Int> {
        return try {
            val userNotificationsSnapshot = userNotificationsRef.child(userId)
                .orderByChild("isRead")
                .equalTo(false)
                .get()
                .await()

            val count = userNotificationsSnapshot.childrenCount.toInt()
            Log.d(TAG, "Unread count for user $userId: $count")

            Result.success(count)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting unread count", e)
            Result.failure(e)
        }
    }
}
