package com.example.musicplayerapplication.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicplayerapplication.Utils.CloudinaryHelper
import kotlinx.coroutines.launch
import com.example.musicplayerapplication.ViewModel.HomeViewModel
import com.example.musicplayerapplication.ViewModel.MusicViewModel
import com.example.musicplayerapplication.ViewModel.MusicViewModelFactory
import com.example.musicplayerapplication.ViewModel.ProfileViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Cloudinary if not already initialized
        if (!CloudinaryHelper.isInitialized()) {
            try {
                CloudinaryHelper.initialize(
                    context = applicationContext,
                    cloudName = "drfit5xud",
                    apiKey = "649351633944394",
                    apiSecret = "dOKyZ9LYkoLKpkgP1zGs0oitL_k"
                )
                Log.d("DashboardActivity", "Cloudinary initialized successfully")
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Failed to initialize Cloudinary", e)
            }
        }

        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val musicViewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory(context.applicationContext as Context))

    var selectedIndex by remember { mutableStateOf(0) }
    var showNotificationScreen by remember { mutableStateOf(false) }

    val playbackState by musicViewModel.playbackState.collectAsState()
    val currentSong = playbackState.currentSong

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var showNowPlaying by remember { mutableStateOf(false) }

    // Auto-show bottom sheet when song starts playing
    LaunchedEffect(currentSong) {
        if (currentSong != null && !showNowPlaying) {
            showNowPlaying = true
            sheetState.expand()
        }
    }

    data class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
    val listItem = listOf(
        NavItem(label = "Home", icon = Icons.Default.Home),
        NavItem(label = "Library", icon = Icons.Default.LibraryMusic),
        NavItem(label = "Playlist", icon = Icons.Default.MusicNote),
        NavItem(label = "Saved", icon = Icons.Default.Favorite),
        NavItem(label = "Profile", icon = Icons.Default.Person)
    )

    Scaffold(
        containerColor = Color(0xFF21133B),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF6B21A8)
            ) {
                listItem.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label, fontSize = 12.sp) },
                        onClick = {
                            selectedIndex = index
                            showNotificationScreen = false
                        },
                        selected = selectedIndex == index && !showNotificationScreen,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color(0xFF9C27B0),
                            unselectedTextColor = Color(0xFF9C27B0),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF21133B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Main Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (showNotificationScreen) {
                        NotificationScreen(
                            onBackClick = { showNotificationScreen = false }
                        )
                    } else {
                        when (selectedIndex) {
                            0 -> {
                                HomeScreen(
                                    musicViewModel = musicViewModel,
                                    onNotificationClick = { showNotificationScreen = true },
                                    onSearchClick = { }
                                )
                            }
                            1 -> {
                                LibraryScreen(musicViewModel = musicViewModel)
                            }
                            2 -> {
                                PlaylistScreen(musicViewModel = musicViewModel)
                            }
                            3 -> {
                                // Saved Screen - Navigate to SavedScreen Activity
                                val intent = Intent(context, SavedScreen::class.java)
                                context.startActivity(intent)
                                // Reset selection to previous
                                selectedIndex = 0
                            }
                            4 -> {
                                ProfileScreen()
                            }
                        }
                    }
                }

                // Mini Player
                if (currentSong != null && !showNowPlaying) {
                    MiniPlayer(
                        song = currentSong,
                        isPlaying = playbackState.isPlaying,
                        onPlayPauseClick = {
                            if (playbackState.isPlaying) {
                                musicViewModel.pause()
                            } else {
                                musicViewModel.resume()
                            }
                        },
                        onClick = {
                            showNowPlaying = true
                            scope.launch {
                                sheetState.expand()
                            }
                        }
                    )
                }
            }

            // Modal Bottom Sheet for Now Playing
            if (showNowPlaying && currentSong != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showNowPlaying = false
                    },
                    sheetState = sheetState,
                    containerColor = Color.Transparent,
                    dragHandle = null
                ) {
                    NowPlayingScreen(
                        song = currentSong,
                        isPlaying = playbackState.isPlaying,
                        currentPosition = playbackState.currentPosition,
                        duration = currentSong.duration,
                        shuffleEnabled = playbackState.isShuffleEnabled,
                        repeatMode = playbackState.repeatMode,
                        onPlayPauseClick = {
                            if (playbackState.isPlaying) {
                                musicViewModel.pause()
                            } else {
                                musicViewModel.resume()
                            }
                        },
                        onSeekTo = { position ->
                            musicViewModel.seekTo(position)
                        },
                        onBackClick = {
                            scope.launch {
                                sheetState.hide()
                                showNowPlaying = false
                            }
                        },
                        onSkipNext = { musicViewModel.skipToNext() },
                        onSkipPrevious = { musicViewModel.skipToPrevious() },
                        onToggleFavorite = {
                            currentSong.let { musicViewModel.toggleFavorite(it) }
                        },
                        onToggleShuffle = { musicViewModel.toggleShuffle() },
                        onToggleRepeat = { musicViewModel.toggleRepeatMode() },
                        onAudioEffectsClick = {
                            val intent = Intent(context, AudioEffectsScreen::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

// NotificationScreen is now imported from NotificationScreen.kt
// Removed duplicate implementation

@Composable
fun MiniPlayer(
    song: com.example.musicplayerapplication.model.Song,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF6B4FA0)),
                contentAlignment = Alignment.Center
            ) {
                if (song.coverUrl.isNotEmpty()) {
                    AsyncImage(
                        model = song.coverUrl,
                        contentDescription = song.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
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
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = song.artist,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            // Play/Pause Button
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}