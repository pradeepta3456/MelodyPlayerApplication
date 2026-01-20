package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.Notification
import com.example.musicplayerapplication.model.RecentActivity
import com.example.musicplayerapplication.repository.NotificationRepo
import com.example.musicplayerapplication.repository.NotificationRepoImpl
import kotlinx.coroutines.launch


class NotificationViewModel(
    private val repository: NotificationRepo = NotificationRepoImpl()
) : ViewModel() {

    val recentActivities: List<RecentActivity> = repository.getRecentActivities()
    val notifications = mutableStateListOf<Notification>()
    var errorMessage = mutableStateOf<String?>(null)

    init {
        loadNotifications()
    }

    /**
     * Load notifications
     */
    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                notifications.clear()
                notifications.addAll(repository.getNotifications())
            } catch (e: Exception) {
                errorMessage.value = "Failed to load notifications"
            }
        }
    }

    /**
     * Mark notification as read
     */
    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            try {
                repository.markAsRead(notificationId)
                val index = notifications.indexOfFirst { it.id == notificationId }
                if (index != -1) {
                    notifications[index] = notifications[index].copy(isRead = true)
                }
            } catch (e: Exception) {
                errorMessage.value = "Failed to mark as read"
            }
        }
    }

    /**
     * Delete notification
     */
    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(notificationId)
                notifications.removeIf { it.id == notificationId }
            } catch (e: Exception) {
                errorMessage.value = "Failed to delete notification"
            }
        }
    }

    /**
     * Clear all notifications
     */
    fun clearAllNotifications() {
        viewModelScope.launch {
            try {
                repository.clearAllNotifications()
                notifications.clear()
            } catch (e: Exception) {
                errorMessage.value = "Failed to clear notifications"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage.value = null
    }
}
