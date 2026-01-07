package com.example.musicplayerapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.model.Notification
import com.example.musicplayerapplication.model.RecentActivity
import com.example.musicplayerapplication.repository.NotificationRepo
import com.example.musicplayerapplication.repository.NotificationRepoImpl

class NotificationViewModel(
    private val repository: NotificationRepo = NotificationRepoImpl()
) : ViewModel() {

    val recentActivities: List<RecentActivity> = repository.getRecentActivities()
    val notifications: List<Notification> = repository.getNotifications()
}

