package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.model.Notification
import com.example.musicplayerapplication.model.NotificationType
import com.example.musicplayerapplication.model.RecentActivity

class NotificationRepoImpl : NotificationRepo {

    private val recentActivities = listOf(
        RecentActivity(1, "Playlist added successfully", R.drawable.img_1),
        RecentActivity(2, "Review your songs playlist", R.drawable.img_1),
        RecentActivity(3, "Album List is updated", R.drawable.img_1),
        RecentActivity(4, "All songs included in favourite", R.drawable.img_1)
    )

    private val notifications = mutableListOf(
        Notification(
            1,
            "Songs added to your newly created playlist",
            true,
            R.drawable.img_1,
            0xFFB0B0B0,
            System.currentTimeMillis(),
            false,
            NotificationType.SUCCESS
        ),
        Notification(
            2,
            "Achievement unlocked. Checkout your profile for more information",
            false,
            0,
            0xFF9E9E9E,
            System.currentTimeMillis() - 3600000,
            false,
            NotificationType.ACHIEVEMENT
        ),
        Notification(
            3,
            "New songs available in your favorite genre",
            false,
            0,
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
