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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
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
        containerColor = Color(0xFF0A0E27),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 16.dp),
                color = Color(0xFF1A1F3A)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1A1F3A),
                                    Color(0xFF242B4A)
                                )
                            )
                        )
                        .padding(vertical = 8.dp)
                ) {
                    listItem.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = if (selectedIndex == index && !showNotificationScreen)
                                                Color(0xFF6366F1).copy(alpha = 0.2f)
                                            else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selectedIndex == index && !showNotificationScreen)
                                            Color(0xFF818CF8)
                                        else Color(0xFF64748B)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedIndex == index && !showNotificationScreen)
                                        FontWeight.SemiBold
                                    else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedIndex = index
                                showNotificationScreen = false
                            },
                            selected = selectedIndex == index && !showNotificationScreen,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF818CF8),
                                selectedTextColor = Color(0xFFF1F5F9),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF94A3B8),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() }
            .shadow(elevation = 12.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            Color(0xFF334155)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Album Art with gradient overlay
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF6366F1),
                                            Color(0xFF8B5CF6)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = song.title,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Song Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = song.title,
                        color = Color(0xFFF8FAFC),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song.artist,
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Play/Pause Button with modern styling
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        )
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clickable { onPlayPauseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}