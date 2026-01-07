package com.example.musicplayerapplication.model

data class Notification(
    val message: String,
    val hasImage: Boolean = false,
    val imageRes: Int = 0,
    val textColor: Long = 0xFFB0B0B0 // Store as Long to avoid Color dependency in model
)

