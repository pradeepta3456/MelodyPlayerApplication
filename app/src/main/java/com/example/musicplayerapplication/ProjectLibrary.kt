package com.example.musicplayerapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class ProjectLibrary : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistScreenActivity()
        }
    }
}

@Composable
fun PlaylistScreen() {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF6AD0A6), Color(0xFF043454))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(20.dp)
    ) {
        // BACK BUTTON
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // PLAYLIST IMAGE BOX
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_library_music_24),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(70.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Chill Vibes",
            color = Color.White,
            fontSize = 26.sp
        )

        Text(
            text = "Your perfect relaxation mix",
            color = Color.White.copy(0.8f),
            fontSize = 16.sp
        )

        Text(
            text = "5 songs · 21 min",
            color = Color.White.copy(0.8f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        // TOP ACTION BUTTONS
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58E1C3))
            ) {
                Text("Play")
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.FavoriteBorder, "", tint = Color.White)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Download, "", tint = Color.White)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Share, "", tint = Color.White)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreVert, "", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // SONG LIST - Fixed: img parameter is now Int instead of String
        SongItem(1, R.drawable.kissme, "kissme", "Red Love")
        SongItem(2, R.drawable.lana, "radio", "Lana Del Rey")
        SongItem(3, R.drawable.larosea, "Face", "Larosea")
    }
}

@Composable
fun SongItem(number: Int, img: Int, title: String, artist: String) {  // <- Changed img: String to img: Int
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Text(
            text = "$number.",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.width(30.dp)
        )

        Image(
            painter = painterResource(id = img),  // This works now because img is Int
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column {
            Text(text = title, color = Color.White, fontSize = 18.sp)
            Text(text = artist, color = Color.White.copy(0.8f), fontSize = 14.sp)
        }
    }
}