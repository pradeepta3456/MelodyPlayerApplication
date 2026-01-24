package com.example.musicplayerapplication.View

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicplayerapplication.ViewModel.MusicViewModel
import com.example.musicplayerapplication.ViewModel.MusicViewModelFactory
import com.example.musicplayerapplication.ViewModel.SavedViewModel
import com.example.musicplayerapplication.ViewModel.SavedViewModelFactory
import com.example.musicplayerapplication.View.components.StandardSongCard
import com.example.musicplayerapplication.model.Song

class SavedScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavedScreenContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreenContent() {
    val context = LocalContext.current
    val savedViewModel: SavedViewModel = viewModel(factory = SavedViewModelFactory())
    val musicViewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory(context))

    val savedSongs by savedViewModel.savedSongs.collectAsState()
    val isLoading by savedViewModel.isLoading.collectAsState()
    val errorMessage by savedViewModel.errorMessage.collectAsState()
    val favoritesCount by savedViewModel.favoritesCount.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF1A1F3A),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar with modern styling
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp),
                color = Color.Transparent
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFEF4444),
                                                Color(0xFFEC4899)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Saved",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    "Saved Songs",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFF8FAFC)
                                )
                                Text(
                                    "$favoritesCount ${if (favoritesCount == 1) "song" else "songs"}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { (context as? ComponentActivity)?.finish() },
                            modifier = Modifier
                                .padding(8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                "Back",
                                tint = Color(0xFFF1F5F9),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    actions = {
                        // Refresh button
                        IconButton(
                            onClick = { savedViewModel.loadSavedSongs() },
                            modifier = Modifier
                                .padding(4.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color(0xFF818CF8),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        // Clear all button
                        if (savedSongs.isNotEmpty()) {
                            IconButton(
                                onClick = { showClearDialog = true },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .padding(end = 8.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Clear All",
                                    tint = Color(0xFFF87171),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    isLoading -> {
                        // Loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFFFF6B6B),
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    "Loading saved songs...",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    savedSongs.isEmpty() -> {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "No favorites",
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(80.dp)
                                )
                                Text(
                                    "No Saved Songs Yet",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Songs you mark as favorite will appear here",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.6f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        // Songs list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(savedSongs, key = { it.id }) { song ->
                                StandardSongCard(
                                    song = song,
                                    isFavorite = true,
                                    onSongClick = {
                                        musicViewModel.setPlaylist(savedSongs)
                                        musicViewModel.playSong(song)
                                    },
                                    onPlayClick = {
                                        musicViewModel.setPlaylist(savedSongs)
                                        musicViewModel.playSong(song)
                                    },
                                    onSaveClick = {
                                        savedViewModel.removeFromFavorites(song.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Clear all confirmation dialog
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = {
                    Text(
                        "Clear All Favorites?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to remove all $favoritesCount songs from your favorites? This action cannot be undone.",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            savedViewModel.clearAllFavorites()
                            showClearDialog = false
                        }
                    ) {
                        Text("Clear All", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancel", color = Color(0xFF00D9FF))
                    }
                },
                containerColor = Color(0xFF2A2A3E)
            )
        }

        // Error message snackbar
        errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Show error and clear it
                kotlinx.coroutines.delay(3000)
                savedViewModel.clearError()
            }
        }
    }
}

@Composable
fun SavedSongCard(
    song: Song,
    onSongClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSongClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2A47).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Album Art
            if (song.coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = song.title,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF8B5CF6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = song.title,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Song Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = song.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (song.album.isNotEmpty()) {
                        Text(
                            text = song.album,
                            fontSize = 12.sp,
                            color = Color(0xFF00D9FF),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    Text(
                        text = song.durationFormatted,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            // Remove button
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
