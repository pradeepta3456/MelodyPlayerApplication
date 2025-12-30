package com.example.musicplayerapplication

import com.example.musicplayerapplication.viewmodel.Playlist
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
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.ui.theme.Purple40




@Composable
fun PlaylistScreenActivity() {

    val PurpleBg = Color(0xFF8B5CF6)

    val allSongs = remember {
        mutableStateListOf(
            Song(1, "kissme", "Red Love", R.drawable.kissme),
            Song(2, "radio", "Lana Del Rey", R.drawable.lana),
            Song(3, "Face", "Larosea", R.drawable.larosea),
            Song(4, "Sunset Dreams", "Ambient Collective", R.drawable.baseline_library_music_24),
            Song(5, "Midnight Coffee", "Jazz Essentials", R.drawable.baseline_library_music_24)
        )
    }

    // Create playlists with proper state management
    val playlists = remember {
        mutableStateListOf(
            Playlist(
                id = 1,
                name = "Chill Vibes",
                description = "Your perfect relaxation mix",
                icon = Icons.Default.LibraryMusic,
                gradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6AD0A6), Color(0xFF043454))
                ),
                songs = mutableStateListOf<Song>().apply {
                    addAll(allSongs.take(5))
                }
            ),
            Playlist(
                id = 2,
                name = "Favorite Songs",
                description = "Your most loved tracks",
                icon = Icons.Default.Favorite,
                gradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFFEC4899), Color(0xFFF43F5E))
                ),
                songs = mutableStateListOf<Song>()
            ),
            Playlist(
                id = 3,
                name = "Downloaded",
                description = "Available offline",
                icon = Icons.Default.Download,
                gradient = Brush.verticalGradient(
                    colors = listOf(PurpleBg, PurpleBg)
                ),
                songs = mutableStateListOf<Song>()
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
            .background(Purple40)
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
                text = "My Playlist",
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
            .background(Purple40)
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
    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn {
            itemsIndexed(playlist.songs) { index, song ->
                SongItem(
                    number = index + 1,
                    song = song,
                    onFavoriteClick = {
                        val favPlayList = playlists.find { it.name == "Favorite Songs" }
                        val actualSong = allSongs.find { it.id == song.id }

                        actualSong?.let {
                            it.isFavorite = !it.isFavorite
                            if (it.isFavorite) {
                                if (favPlayList?.songs?.removeAll { s -> s.id == it.id } == true) {
                                    favPlayList.songs.add(it)
                                }
                            } else {
                                favPlayList?.songs?.removeAll { s -> s.id == it.id }
                            }
                        }
                    },

                    onDownloadClick = {
                        val downloadPlayList = playlists.find { it.name == "Downloaded" }
                        val actualSong = allSongs.find { it.id == song.id }

                        actualSong?.let {
                            it.isDownloaded = !it.isDownloaded

                            if (it.isDownloaded) {
                                if (downloadPlayList?.songs?.any { s -> s.id == it.id } == false) {
                                    downloadPlayList.songs.add(it)
                                }
                            } else {
                                downloadPlayList?.songs?.removeAll { s -> s.id == it.id }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SongItem(
    number: Int,
    song: Song,
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
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
            painter = painterResource(id = song.cover),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = song.artist,
                color = Color.White.copy(0.8f),
                fontSize = 14.sp
            )
        }

        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (song.isFavorite)
                    Icons.Default.Favorite
                else
                    Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (song.isFavorite)
                    Color(0xFFEC4899)
                else
                    Color.White.copy(alpha = 0.7f)
            )
        }

        IconButton(onClick = onDownloadClick) {
            Icon(
                imageVector = if (song.isDownloaded)
                    Icons.Default.CheckCircle
                else
                    Icons.Default.Download,
                contentDescription = "Download",
                tint = if (song.isDownloaded)
                    Color(0xFF58E1C3)
                else
                    Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
