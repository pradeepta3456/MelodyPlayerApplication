package com.example.musicplayerapplication


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayerapplication.model.Playlist
import com.example.musicplayerapplication.model.Song

// Data Models





@Composable
fun Playlist() {
    // All songs database
    val allSongs = remember {
        mutableStateListOf(
            Song(1, "kissme", "Red Love", R.drawable.kissme),
            Song(2, "radio", "Lana Del Rey", R.drawable.lana),
            Song(3, "Face", "Larosea", R.drawable.larosea),
            Song(4, "Sunset Dreams", "Ambient Collective", R.drawable.baseline_library_music_24),
            Song(5, "Midnight Coffee", "Jazz Essentials", R.drawable.baseline_library_music_24)
        )
    }

    // Create playlists
    val playlists = remember {
        listOf(
            Playlist(
                id = 1,
                name = "Chill Vibes",
                description = "Your perfect relaxation mix",
                icon = Icons.Default.LibraryMusic,
                gradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6AD0A6), Color(0xFF043454))
                ),
                songs = allSongs.take(5).toMutableList()
            ),
            Playlist(
                id = 2,
                name = "Favorite Songs",
                description = "Your most loved tracks",
                icon = Icons.Default.Favorite,
                gradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFFEC4899), Color(0xFFF43F5E))
                ),
                songs = mutableStateListOf() // Empty initially
            ),
            Playlist(
                id = 3,
                name = "Downloaded",
                description = "Available offline",
                icon = Icons.Default.Download,
                gradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
                ),
                songs = mutableStateListOf() // Empty initially
            )
        )
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Library) }

    when (val screen = currentScreen) {
        is Screen.Library -> LibraryScreen(
            playlists = playlists,
            allSongs = allSongs,
            onPlaylistClick = { playlist ->
                currentScreen = Screen.PlaylistDetail(playlist)
            }
        )
        is Screen.PlaylistDetail -> PlaylistDetailScreen(
            playlist = screen.playlist,
            allSongs = allSongs,
            playlists = playlists,
            onBackClick = { currentScreen = Screen.Library }
        )
    }
}

sealed class Screen {
    object Library : Screen()
    data class PlaylistDetail(val playlist: Playlist) : Screen()
}

@Composable
fun LibraryScreen(
    playlists: List<Playlist>,
    allSongs: List<Song>,
    onPlaylistClick: (Playlist) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                contentDescription = "Library",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "My Library",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Playlist folders
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(playlists) { playlist ->
                PlaylistCard(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) }
                )
            }
        }
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E293B))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(playlist.gradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = playlist.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Playlist info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = playlist.description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${playlist.songs.size} songs",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    allSongs: MutableList<Song>,
    playlists: List<Playlist>,
    onBackClick: () -> Unit
) {
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(playlist.gradient)
        ) {
            // Header section
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Back button
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Playlist cover
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = playlist.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = playlist.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = playlist.description,
                    fontSize = 16.sp,
                    color = Color.White.copy(0.8f)
                )

                Text(
                    text = "${playlist.songs.size} songs",
                    fontSize = 14.sp,
                    color = Color.White.copy(0.8f)
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58E1C3))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Song list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFF0F172A),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(16.dp)
            ) {
                itemsIndexed(playlist.songs) { index, song ->
                    SongItem(
                        number = index + 1,
                        song = song,
                        allSongs = allSongs,
                        playlists = playlists,
                        onFavoriteClick = {
                            val favPlaylist = playlists.find { it.name == "Favorite Songs" }
                            val actualSong = allSongs.find { it.id == song.id }

                            actualSong?.let {
                                it.isFavorite = !it.isFavorite

                                if (it.isFavorite) {
                                    if (favPlaylist?.songs?.none { s -> s.id == it.id } == true) {
                                        favPlaylist.songs.add(it)
                                    }
                                    snackbarMessage = "Added to Favorites"
                                } else {
                                    favPlaylist?.songs?.removeAll { s -> s.id == it.id }
                                    snackbarMessage = "Removed from Favorites"
                                }
                                showSnackbar = true
                            }
                        },
                        onDownloadClick = {
                            val downloadPlaylist = playlists.find { it.name == "Downloaded" }
                            val actualSong = allSongs.find { it.id == song.id }

                            actualSong?.let {
                                it.isDownloaded = !it.isDownloaded

                                if (it.isDownloaded) {
                                    if (downloadPlaylist?.songs?.none { s -> s.id == it.id } == true) {
                                        downloadPlaylist.songs.add(it)
                                    }
                                    snackbarMessage = "Downloaded"
                                } else {
                                    downloadPlaylist?.songs?.removeAll { s -> s.id == it.id }
                                    snackbarMessage = "Removed from Downloads"
                                }
                                showSnackbar = true
                            }
                        }
                    )

                    if (index < playlist.songs.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Snackbar
        if (showSnackbar) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = snackbarMessage,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }

            LaunchedEffect(showSnackbar) {
                kotlinx.coroutines.delay(2000)
                showSnackbar = false
            }
        }
    }
}

@Composable
fun SongItem(
    number: Int,
    song: Song,
    allSongs: List<Song>,
    playlists: List<Playlist>,
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    val actualSong = allSongs.find { it.id == song.id } ?: song

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$number.",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(30.dp)
        )

        Image(
            painter = painterResource(id = actualSong.cover),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = actualSong.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = actualSong.artist,
                color = Color.White.copy(0.8f),
                fontSize = 14.sp
            )
        }

        // Favorite button
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (actualSong.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (actualSong.isFavorite) Color(0xFFEC4899) else Color.White.copy(alpha = 0.7f)
            )
        }

        // Download button
        IconButton(onClick = onDownloadClick) {
            Icon(
                imageVector = if (actualSong.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                contentDescription = "Download",
                tint = if (actualSong.isDownloaded) Color(0xFF58E1C3) else Color.White.copy(alpha = 0.7f)
            )
        }
    }
}