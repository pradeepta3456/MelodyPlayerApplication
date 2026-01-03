package com.example.musicplayerapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data classes
data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: String
)

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val songCount: Int,
    val songs: List<Song>,
    val isAiGenerated: Boolean = false
)

// Main Activity
class PlaylistScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerApp()
        }
    }
}

@Composable
fun MusicPlayerApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.PlaylistList) }
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    when (currentScreen) {
        is Screen.PlaylistList -> {
            PlaylistScreenContent(
                onPlaylistClick = { playlist ->
                    selectedPlaylist = playlist
                    currentScreen = Screen.PlaylistDetail
                },
                onQuickPlay = { playlist ->
                    selectedPlaylist = playlist
                    currentSong = playlist.songs.firstOrNull()
                    isPlaying = true
                    currentScreen = Screen.NowPlaying
                }
            )
        }
        is Screen.PlaylistDetail -> {
            selectedPlaylist?.let { playlist ->
                PlaylistDetailScreen(
                    playlist = playlist,
                    onBack = { currentScreen = Screen.PlaylistList },
                    onSongClick = { song ->
                        currentSong = song
                        isPlaying = true
                        currentScreen = Screen.NowPlaying
                    },
                    onPlayAll = {
                        currentSong = playlist.songs.firstOrNull()
                        isPlaying = true
                        currentScreen = Screen.NowPlaying
                    }
                )
            }
        }
        is Screen.NowPlaying -> {
            currentSong?.let { song ->
                NowPlayingScreen(
                    song = song,
                    playlist = selectedPlaylist,
                    isPlaying = isPlaying,
                    onPlayPauseClick = { isPlaying = !isPlaying },
                    onBack = { currentScreen = Screen.PlaylistDetail },
                    onNext = {
                        selectedPlaylist?.songs?.let { songs ->
                            val currentIndex = songs.indexOf(song)
                            if (currentIndex < songs.size - 1) {
                                currentSong = songs[currentIndex + 1]
                            }
                        }
                    },
                    onPrevious = {
                        selectedPlaylist?.songs?.let { songs ->
                            val currentIndex = songs.indexOf(song)
                            if (currentIndex > 0) {
                                currentSong = songs[currentIndex - 1]
                            }
                        }
                    }
                )
            }
        }
    }
}

sealed class Screen {
    object PlaylistList : Screen()
    object PlaylistDetail : Screen()
    object NowPlaying : Screen()
}

// Sample Data
fun getSamplePlaylists(): List<Playlist> {
    return listOf(
        Playlist(
            id = 1,
            name = "Focus Flow",
            description = "AI curated for productivity",
            songCount = 32,
            isAiGenerated = true,
            songs = listOf(
                Song(1, "Deep Focus", "Ambient Collective", "4:32"),
                Song(2, "Concentration Mode", "Study Beats", "3:45"),
                Song(3, "Mind Flow", "Zen Masters", "5:12"),
                Song(4, "Brain Waves", "Focus Music", "4:20")
            )
        ),
        Playlist(
            id = 2,
            name = "Evening Calm",
            description = "Relaxing evening vibes",
            songCount = 15,
            isAiGenerated = true,
            songs = listOf(
                Song(5, "Sunset Dreams", "Chill Artists", "3:28"),
                Song(6, "Evening Breeze", "Smooth Sounds", "4:15"),
                Song(7, "Twilight Hour", "Relaxation Zone", "5:03")
            )
        ),
        Playlist(
            id = 3,
            name = "Chill Vibes",
            description = "Relaxing tunes for any time",
            songCount = 24,
            isAiGenerated = false,
            songs = listOf(
                Song(8, "kissme", "Red Love", "3:12"),
                Song(9, "radio", "Lana Del Rey", "4:28"),
                Song(10, "Face", "Larosea", "3:45"),
                Song(11, "Moonlight", "Indie Dreams", "4:02")
            )
        ),
        Playlist(
            id = 4,
            name = "Workout Energy",
            description = "High energy beats",
            songCount = 18,
            isAiGenerated = false,
            songs = listOf(
                Song(12, "Power Up", "Gym Beats", "3:30"),
                Song(13, "Maximum Drive", "Workout Mix", "3:55"),
                Song(14, "Beast Mode", "Fitness Music", "4:10")
            )
        )
    )
}

@Composable
fun PlaylistScreenContent(
    onPlaylistClick: (Playlist) -> Unit,
    onQuickPlay: (Playlist) -> Unit
) {
    val playlists = remember { getSamplePlaylists() }
    val aiPlaylists = playlists.filter { it.isAiGenerated }
    val userPlaylists = playlists.filter { !it.isAiGenerated }

    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1A0B2E),
            Color(0xFF2D1B4E),
            Color(0xFF3D2766)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        /** HEADER **/
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Playlists",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Button(
                onClick = {},
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB040FF)
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Create", fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(28.dp))

        /** AI SMART PLAYLISTS **/
        Text(
            text = "✨ AI Smart Playlists",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            aiPlaylists.forEach { playlist ->
                AiPlaylistCard(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) },
                    onPlayClick = { onQuickPlay(playlist) }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        /** YOUR PLAYLISTS **/
        Text(
            text = "🎵 Your Playlists",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(Modifier.height(16.dp))

        userPlaylists.forEach { playlist ->
            UserPlaylistRow(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist) }
            )
        }

        Spacer(Modifier.height(32.dp))

        /** QUICK CREATE **/
        Text(
            text = "⚡ Quick Create",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickCreateCard(
                title = "Liked Songs",
                subtitle = "Auto playlist",
                icon = Icons.Default.Favorite,
                gradient = Brush.horizontalGradient(
                    listOf(Color(0xFFE91E63), Color(0xFF9C27B0))
                ),
                modifier = Modifier.weight(1f),
                onClick = {}
            )
            QuickCreateCard(
                title = "Recently Added",
                subtitle = "Last 30 days",
                icon = Icons.Default.AccessTime,
                gradient = Brush.horizontalGradient(
                    listOf(Color(0xFF2196F3), Color(0xFF00BCD4))
                ),
                modifier = Modifier.weight(1f),
                onClick = {}
            )
        }
    }
}

@Composable
fun AiPlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF3D1F5C))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Text(
            text = "✨ AI Generated",
            color = Color(0xFFFFB74D),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = playlist.name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${playlist.songCount} songs",
            color = Color.White.copy(0.6f),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onPlayClick() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB040FF)
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Play", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun UserPlaylistRow(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2D1B4E))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFB040FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = playlist.description,
                color = Color.White.copy(0.6f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${playlist.songCount} songs",
                color = Color.White.copy(0.4f),
                fontSize = 12.sp
            )
        }
    }

    Spacer(Modifier.height(12.dp))
}

@Composable
fun QuickCreateCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .clickable { onClick() }
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            icon,
            null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Column {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(0.8f),
                fontSize = 12.sp
            )
        }
    }
}

// Playlist Detail Screen
@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit,
    onPlayAll: () -> Unit
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF5DBDA6),
            Color(0xFF4A9B8E),
            Color(0xFF2B7A7A),
            Color(0xFF1A5563),
            Color(0xFF0D3947)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Playlist cover art
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF7FC9BC)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Playlist info
        Text(
            text = playlist.name,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = playlist.description,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${playlist.songs.size} songs • 21 min",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(24.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPlayAll,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5DBDA6)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Play",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = "Download",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Song list
        playlist.songs.forEachIndexed { index, song ->
            SongItem(
                index = index + 1,
                song = song,
                onClick = { onSongClick(song) }
            )
        }
    }
}

@Composable
fun SongItem(
    index: Int,
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$index.",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(32.dp)
        )

        Spacer(Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4A9B8E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = song.artist,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Text(
            text = song.duration,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

// Now Playing Screen
@Composable
fun NowPlayingScreen(
    song: Song,
    playlist: Playlist?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1A0B2E),
            Color(0xFF2D1B4E),
            Color(0xFF3D2766)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "Now Playing",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        // Album Art
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFB040FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(Modifier.height(40.dp))

        // Song Info
        Text(
            text = song.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = song.artist,
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(40.dp))

        // Progress bar
        var progress by remember { mutableStateOf(0.3f) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = progress,
                onValueChange = { progress = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFFB040FF),
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1:23", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text(song.duration, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(40.dp))

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = onPrevious) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            FloatingActionButton(
                onClick = onPlayPauseClick,
                containerColor = Color(0xFFB040FF),
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = onNext) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        // Additional controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

}
@Preview(showBackground = true)
@Composable
fun PlaylistScreenActivity1() {
    MusicPlayerApp()
}