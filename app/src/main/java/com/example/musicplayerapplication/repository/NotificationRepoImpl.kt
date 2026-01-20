package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Notification
import com.example.musicplayerapplication.model.NotificationType
import com.example.musicplayerapplication.model.RecentActivity

class NotificationRepoImpl : NotificationRepo {

    private val recentActivities = listOf(
        RecentActivity(1, "Playlist added successfully", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/activities%2Fplaylist_added.jpg?alt=media"),
        RecentActivity(2, "Review your songs playlist", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/activities%2Freview_songs.jpg?alt=media"),
        RecentActivity(3, "Album List is updated", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/activities%2Falbum_updated.jpg?alt=media"),
        RecentActivity(4, "All songs included in favourite", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/activities%2Ffavourite_songs.jpg?alt=media")
    )

    private val notifications = mutableListOf(
        Notification(
            1,
            "Songs added to your newly created playlist",
            true,
            "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/notifications%2Fplaylist_created.jpg?alt=media",
            0xFFB0B0B0,
            System.currentTimeMillis(),
            false,
            NotificationType.SUCCESS
        ),
        Notification(
            2,
            "Achievement unlocked. Checkout your profile for more information",
            false,
            "",
            0xFF9E9E9E,
            System.currentTimeMillis() - 3600000,
            false,
            NotificationType.ACHIEVEMENT
        ),
        Notification(
            3,
            "New songs available in your favorite genre",
            false,
            "",
            0xFFFF6B4A,
            System.currentTimeMillis() - 7200000,
            true,
            NotificationType.INFO
        )
    )

    override fun getRecentActivities(): List<RecentActivity> = recentActivities

    override fun getNotifications(): List<Notification> = notifications

    override fun markAsRead(notificationId: Int) {
        val notification = notifications.find { it.id == notificationId }
        notification?.let {
            val index = notifications.indexOf(it)
            notifications[index] = it.copy(isRead = true)
        }
    }

    override fun deleteNotification(notificationId: Int) {
        notifications.removeIf { it.id == notificationId }
    }

    override fun clearAllNotifications() {
        notifications.clear()
    }
}
