package com.example.musicplayerapplication.model

data class Achievement(
    val id: Int = 0,
    val title: String,
    val description: String,
    val iconUrl: String = "", // Firebase Storage URL
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val target: Int = 100
)
