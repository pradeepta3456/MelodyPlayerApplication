package com.example.musicplayerapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song

import com.example.musicplayerapplication.ui.theme.*
import com.example.musicplayerapplication.viewmodel.HomeViewModel

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MelodyPlayTheme {
                HomeScreen(viewModel = viewModel())
            }
        }
    }
}



@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkPurpleBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Welcome to", color = Color.White.copy(alpha = 0.8f), fontSize = 20.sp)
                Text("Melody", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Featured Album
        item {
            FeaturedAlbumCard("Lunar Eclipse", "Sunset Vibes", R.drawable.img_4)
        }

        // Recently Played Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Recently Played", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Recently Played Songs
        items(viewModel.recentSongs) { song ->
            RecentSongItem(song)
        }

        // Trending Header with Icon
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Trending Up",
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "Trending",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }



        // Trending Albums Horizontal
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(viewModel.trendingAlbums) { album ->
                    TrendingAlbumCard(album)
                }
            }
        }
    }
}

@Composable
fun FeaturedAlbumCard(title: String, artist: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Featured Album", color = TextYellow, fontSize = 12.sp)
                Text(title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(artist, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))

                Button(
                    onClick = { /* Play action */ },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Play Now")
                }
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            )
        }
    }
}

@Composable
fun RecentSongItem(song: Song) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(song.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(song.artist, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    }
}

@Composable
fun TrendingAlbumCard(album: Album) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = album.imageRes),
                contentDescription = album.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(album.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(album.artistVibes, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}