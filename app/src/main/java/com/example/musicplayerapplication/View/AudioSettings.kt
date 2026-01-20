package com.example.musicplayerapplication.View

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun AudioSettings() {
    Text(
        "Audio Settings Page",
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}