package com.example.melodyplay
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class LibraryGuestActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibraryScreen()
        }
    }
}

@Composable
fun LibraryScreen() {
    // Define the custom purple color from your screenshot
    val libraryPurple = Color(0xFF190641) // Adjust hex to match exactly

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(libraryPurple)
            .padding(16.dp)
    ) {
        // Header Section
        Text(
            text = "Your Library",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "All your music in one place",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Toggle Tabs (Albums, Artists, Songs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(color = Color.White.copy(alpha = 0.2f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TabItem("Albums", isSelected = true)
            TabItem("Artists", isSelected = false)
            TabItem("Songs", isSelected = false)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Content Grid
        val albums = listOf(
            Album("Jennifer", "Eclipse Life", R.drawable.img_4),
            Album("Future", "Typa Sz", R.drawable.img_1),
            Album("LadyRock", "Swim", R.drawable.img_8),
            Album("Anton", "Bite Me", R.drawable.img_5)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(albums) { album ->
                AlbumCard(album)
            }

        }
    }

}


@Composable
fun TabItem(label: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun AlbumCard(album: Album) {
    Column {
        Image(
            painter = painterResource(id = album.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp)) // Large rounded corners as seen in image
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = album.artistVibes,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(text = album.title, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
    }
}