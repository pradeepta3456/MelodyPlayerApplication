package com.example.musicplayerapplication.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.Notification
import com.example.musicplayerapplication.repository.NotificationRepository
import com.example.musicplayerapplication.repository.NotificationRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Notification ViewModel
 * Manages notification state and operations following MVVM pattern
 */
class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // State flows
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    companion object {
        private const val TAG = "NotificationViewModel"
    }

    init {
        loadNotifications()
        loadUnreadCount()
    }

    /**
     * Load notifications for current user
     */
    fun loadNotifications() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: run {
                    Log.w(TAG, "User not logged in")
                    return@launch
                }

                _isLoading.value = true
                _errorMessage.value = null

                Log.d(TAG, "Loading notifications for user: $userId")

                val result = repository.getUserNotifications(userId, limit = 50)

                result.onSuccess { notificationList ->
                    Log.d(TAG, "Loaded ${notificationList.size} notifications")
                    _notifications.value = notificationList
                }.onFailure { error ->
                    Log.e(TAG, "Failed to load notifications", error)
                    _errorMessage.value = "Failed to load notifications: ${error.message}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading notifications", e)
                _errorMessage.value = "Error loading notifications: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load unread notification count
     */
    fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                val result = repository.getUnreadCount(userId)

                result.onSuccess { count ->
                    Log.d(TAG, "Unread count: $count")
                    _unreadCount.value = count
                }.onFailure { error ->
                    Log.e(TAG, "Failed to get unread count", error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting unread count", e)
            }
        }
    }

    /**
     * Mark notification as read
     */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                Log.d(TAG, "Marking notification as read: $notificationId")

                val result = repository.markAsRead(userId, notificationId)

                result.onSuccess {
                    // Update local state
                    _notifications.update { notifications ->
                        notifications.map { notification ->
                            if (notification.id == notificationId) {
                                notification.copy(isRead = true)
                            } else {
                                notification
                            }
                        }
                    }

                    // Update unread count
                    loadUnreadCount()

                    Log.d(TAG, "Successfully marked as read")
                }.onFailure { error ->
                    Log.e(TAG, "Failed to mark as read", error)
                    _errorMessage.value = "Failed to mark as read: ${error.message}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error marking as read", e)
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     * Mark all notifications as read
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                Log.d(TAG, "Marking all notifications as read")

                val result = repository.markAllAsRead(userId)

                result.onSuccess {
                    // Update local state
                    _notifications.update { notifications ->
                        notifications.map { it.copy(isRead = true) }
                    }

                    _unreadCount.value = 0

                    Log.d(TAG, "Successfully marked all as read")
                }.onFailure { error ->
                    Log.e(TAG, "Failed to mark all as read", error)
                    _errorMessage.value = "Failed to mark all as read: ${error.message}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error marking all as read", e)
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     * Delete notification
     */
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                Log.d(TAG, "Deleting notification: $notificationId")

                val result = repository.deleteNotification(userId, notificationId)

                result.onSuccess {
                    // Update local state
                    _notifications.update { notifications ->
                        notifications.filter { it.id != notificationId }
                    }

                    // Update unread count
                    loadUnreadCount()

                    Log.d(TAG, "Successfully deleted notification")
                }.onFailure { error ->
                    Log.e(TAG, "Failed to delete notification", error)
                    _errorMessage.value = "Failed to delete: ${error.message}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting notification", e)
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     * Clear all notifications
     */
    fun clearAllNotifications() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                Log.d(TAG, "Clearing all notifications")

                val result = repository.clearAllNotifications(userId)

                result.onSuccess {
                    _notifications.value = emptyList()
                    _unreadCount.value = 0

                    Log.d(TAG, "Successfully cleared all notifications")
                }.onFailure { error ->
                    Log.e(TAG, "Failed to clear all notifications", error)
                    _errorMessage.value = "Failed to clear all: ${error.message}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing all notifications", e)
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

/**
 * ViewModelFactory for NotificationViewModel
 */
class NotificationViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            val repository = NotificationRepositoryImpl()
            return NotificationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
