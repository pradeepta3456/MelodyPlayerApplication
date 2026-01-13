package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.model.Notification
import com.example.musicplayerapplication.model.RecentActivity

class NotificationRepoImpl : NotificationRepo {

    override fun getRecentActivities(): List<RecentActivity> {
        return listOf(
            RecentActivity("Playlist added sucessfully", R.drawable.img_1),
            RecentActivity("Review your songs playlist", R.drawable.img_1),
            RecentActivity("Review your songs playlist", R.drawable.img_1),
            RecentActivity("Review your songs playlist", R.drawable.img_1),
            RecentActivity("Album List is updated", R.drawable.img_1),
            RecentActivity("All songs included in favourite", R.drawable.img_1)
        )
    }

    override fun getNotifications(): List<Notification> {
        return listOf(
            Notification(
                message = "Songs added to your newly created playlist",
                hasImage = true,
                imageRes = R.drawable.img_1,
                textColor = 0xFFB0B0B0 // Light gray
            ),
            Notification(
                message = "Achievement unlocked. Checkout your profile for more information",
                hasImage = false,
                textColor = 0xFF9E9E9E // Darker gray
            ),
            Notification(
                message = "Achievement unlocked. Checkout your profile for more information",
                hasImage = false,
                textColor = 0xFFFF6B4A // Reddish-orange
            ),
            Notification(
                message = "Achievement unlocked. Checkout your profile for more information",
                hasImage = false,
                textColor = 0xFFFFB84A // Yellowish-orange
            )
        )
    }
}

