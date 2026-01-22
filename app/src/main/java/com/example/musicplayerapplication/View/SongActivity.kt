package com.example.musicplayerapplication.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.musicplayerapplication.ui.theme.MusicPlayerApplicationTheme

class SongActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerApplicationTheme {
                DashboardScreen()
            }
        }
    }
}

data class Song(
    val name: String,
    val artist: String,
    val duration: String,
    val imageUrl: String
)

enum class FilterTab {
    SONGS, ARTISTS, ALBUMS, GENRES, FOLDERS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(FilterTab.SONGS) }

    // All songs now come from Firebase
    val songs = remember { emptyList<Song>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2D1B69))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Artist, song and names",
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    IconButton(onClick = { /* Menu action */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.Gray
                        )
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { /* Search action */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF4A3A7A),
                    unfocusedContainerColor = Color(0xFF4A3A7A),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == FilterTab.SONGS,
                    onClick = { selectedTab = FilterTab.SONGS },
                    label = "Songs",
                    icon = Icons.Default.MusicNote
                )
                FilterChip(
                    selected = selectedTab == FilterTab.ARTISTS,
                    onClick = { selectedTab = FilterTab.ARTISTS },
                    label = "Artists",
                    icon = Icons.Default.Person
                )
                FilterChip(
                    selected = selectedTab == FilterTab.ALBUMS,
                    onClick = { selectedTab = FilterTab.ALBUMS },
                    label = "Albums",
                    icon = Icons.Default.Album
                )
                FilterChip(
                    selected = selectedTab == FilterTab.GENRES,
                    onClick = { selectedTab = FilterTab.GENRES },
                    label = "Genres",
                    icon = Icons.Default.LibraryMusic
                )
                FilterChip(
                    selected = selectedTab == FilterTab.FOLDERS,
                    onClick = { selectedTab = FilterTab.FOLDERS },
                    label = "Folders",
                    icon = Icons.Default.Folder
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Songs List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(songs) { song ->
                    SongItem(song = song)
                }

                // AI Discover Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AIDiscoverCard()
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF8B5CF6) else Color(0xFF4A3A7A),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun SongItem(song: Song) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Play song */ },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF3D2B6B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF6B4FA0))
            ) {
                // Placeholder for album art
                // In a real app, use AsyncImage with song.imageUrl
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Album Art",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Song Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Duration
            Text(
                text = song.duration,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AIDiscoverCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF4A3A7A)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sparkle Icon
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "AI Discover",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Based on your listening habits, you might enjoy this genre: pop, latinpop, R&B",
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}