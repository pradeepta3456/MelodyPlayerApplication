package com.example.melodyplay.model

data class Song(val name: String, val artist: String, val imageRes: Int, val plays: Int)
data class Artist(val name: String, val imageRes: Int, val plays: Int)
data class Achievement(val title: String, val description: String, val iconRes: Int, val isCompleted: Boolean)

