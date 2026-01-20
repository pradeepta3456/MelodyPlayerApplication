package com.example.musicplayerapplication.Utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Utility Functions and Extensions
 * Package: Utils (following your 4-package structure)
 */

/**
 * Format duration in milliseconds to MM:SS format
 */
fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    return String.format("%d:%02d", minutes, seconds)
}

/**
 * Format time in seconds to MM:SS format
 */
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}

/**
 * Format timestamp to relative time (e.g., "2 hours ago")
 */
fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            "$minutes minute${if (minutes > 1) "s" else ""} ago"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "$hours hour${if (hours > 1) "s" else ""} ago"
        }
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "$days day${if (days > 1) "s" else ""} ago"
        }
        else -> {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateFormat.format(Date(timestamp))
        }
    }
}

/**
 * Format file size to human readable format
 */
fun formatFileSize(bytes: Long): String {
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        bytes < kb -> "$bytes B"
        bytes < mb -> String.format("%.2f KB", bytes / kb)
        bytes < gb -> String.format("%.2f MB", bytes / mb)
        else -> String.format("%.2f GB", bytes / gb)
    }
}

/**
 * Show toast message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Show long toast message
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Check if string is valid email
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    return this.matches(emailRegex.toRegex())
}

/**
 * Check if string is valid password (min 6 characters)
 */
fun String.isValidPassword(): Boolean {
    return this.length >= 6
}

/**
 * Capitalize first letter of each word
 */
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
    }
}

/**
 * Convert milliseconds to hours
 */
fun Long.toHours(): Long {
    return TimeUnit.MILLISECONDS.toHours(this)
}

/**
 * Convert milliseconds to minutes
 */
fun Long.toMinutes(): Long {
    return TimeUnit.MILLISECONDS.toMinutes(this)
}

/**
 * Safe divide to avoid division by zero
 */
fun safeDivide(numerator: Int, denominator: Int, default: Float = 0f): Float {
    return if (denominator != 0) numerator.toFloat() / denominator else default
}

/**
 * Generate random color
 */
fun generateRandomColor(): Long {
    val random = Random()
    val red = random.nextInt(256)
    val green = random.nextInt(256)
    val blue = random.nextInt(256)
    return (0xFF000000 or (red.toLong() shl 16) or (green.toLong() shl 8) or blue.toLong())
}

/**
 * Truncate string to max length with ellipsis
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        "${this.substring(0, maxLength)}..."
    } else {
        this
    }
}

/**
 * Get file extension from filename
 */
fun String.getFileExtension(): String {
    return this.substringAfterLast('.', "")
}

/**
 * Check if file is audio
 */
fun String.isAudioFile(): Boolean {
    val audioExtensions = listOf("mp3", "wav", "flac", "m4a", "aac", "ogg", "wma")
    return audioExtensions.contains(this.getFileExtension().lowercase())
}

/**
 * Format play count (e.g., 1.5K, 2.3M)
 */
fun formatPlayCount(count: Int): String {
    return when {
        count < 1000 -> count.toString()
        count < 1000000 -> String.format("%.1fK", count / 1000.0)
        else -> String.format("%.1fM", count / 1000000.0)
    }
}

/**
 * Validate URL
 */
fun String.isValidUrl(): Boolean {
    val urlRegex = "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)\$"
    return this.matches(urlRegex.toRegex())
}

/**
 * Get greeting based on time of day
 */
fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}

/**
 * Debounce function for search
 */
class Debouncer(private val delayMs: Long = 300) {
    private var lastRunTime = 0L

    fun debounce(action: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRunTime >= delayMs) {
            lastRunTime = currentTime
            action()
        }
    }
}

/**
 * Constants
 */
object Constants {
    const val MAX_UPLOAD_SIZE_MB = 50
    const val SEARCH_DEBOUNCE_MS = 300L
    const val DEFAULT_PAGE_SIZE = 20
    const val MAX_RECENT_SEARCHES = 10
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_PLAYLIST_NAME_LENGTH = 50
    const val DEFAULT_PLAYBACK_SPEED = 1.0f

    // Audio quality settings
    const val QUALITY_LOW = "Low (64kbps)"
    const val QUALITY_MEDIUM = "Medium (128kbps)"
    const val QUALITY_HIGH = "High (256kbps)"
    const val QUALITY_VERY_HIGH = "Very High (320kbps)"

    // Theme options
    const val THEME_LIGHT = "Light"
    const val THEME_DARK = "Dark"
    const val THEME_AUTO = "Auto"
}

/**
 * Result wrapper for async operations
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Network state
 */
sealed class NetworkState {
    object Available : NetworkState()
    object Unavailable : NetworkState()
}

/**
 * Permission state
 */
sealed class PermissionState {
    object Granted : PermissionState()
    object Denied : PermissionState()
    object PermanentlyDenied : PermissionState()
}