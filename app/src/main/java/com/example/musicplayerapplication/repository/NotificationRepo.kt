package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Notification
import com.example.musicplayerapplication.model.RecentActivity

interface NotificationRepo {
    fun getRecentActivities(): List<RecentActivity>
    fun getNotifications(): List<Notification>
}

