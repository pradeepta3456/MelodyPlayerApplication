package com.example.musicplayerapplication.View

import android.content.Context
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.ViewModel.MusicViewModel
import com.example.musicplayerapplication.ViewModel.MusicViewModelFactory

// Data classes (local to this file since they're specific to playlist feature)
data class PlaylistSong(
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
    val songs: List<PlaylistSong>,
    val isAiGenerated: Boolean = false
)

// Main Playlist Screen - Use this in DashboardActivity
@Composable
fun PlaylistScreen() {
    val context = LocalContext.current
    val musicViewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory(context.applicationContext as Context))
    val allSongs by musicViewModel.allSongs.collectAsState()

    var currentView by remember { mutableStateOf<PlaylistView>(PlaylistView.List) }
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // User-created playlists
    var userPlaylists by remember { mutableStateOf<List<Playlist>>(emptyList()) }

    // Convert Firebase songs to playlists
    val playlists by remember {
        derivedStateOf {
            val firebasePlaylists = mutableListOf<Playlist>()

            // Create playlists by genre
            allSongs.groupBy { it.genre }.forEach { (genre, songs) ->
                if (genre.isNotEmpty() && songs.isNotEmpty()) {
                    firebasePlaylists.add(
                        Playlist(
                            id = firebasePlaylists.size + 1,
                            name = genre,
                            description = "Songs in $genre genre",
                            songCount = songs.size,
                            songs = songs.map { song ->
                                PlaylistSong(
                                    id = song.id.hashCode(),
                                    title = song.title,
                                    artist = song.artist,
                                    duration = song.durationFormatted
                                )
                            },
                            isAiGenerated = true
                        )
                    )
                }
            }

            // Create playlists by artist
            allSongs.groupBy { it.artist }.entries.take(2).forEach { (artist, songs) ->
                if (artist.isNotEmpty() && songs.isNotEmpty()) {
                    firebasePlaylists.add(
                        Playlist(
                            id = firebasePlaylists.size + 1,
                            name = "$artist's Collection",
                            description = "All songs by $artist",
                            songCount = songs.size,
                            songs = songs.map { song ->
                                PlaylistSong(
                                    id = song.id.hashCode(),
                                    title = song.title,
                                    artist = song.artist,
                                    duration = song.durationFormatted
                                )
                            },
                            isAiGenerated = false
                        )
                    )
                }
            }

            firebasePlaylists + getPlaylistData() + userPlaylists
        }
    }

    // Audio settings state
    var bassLevel by remember { mutableStateOf(0f) }
    var trebleLevel by remember { mutableStateOf(0f) }
    var volumeLevel by remember { mutableStateOf(0.7f) }

    when (currentView) {
        is PlaylistView.List -> {
            PlaylistListContent(
                playlists = playlists,
                onPlaylistClick = { playlist ->
                    selectedPlaylist = playlist
                    currentView = PlaylistView.Detail
                },
                onQuickPlay = { playlist ->
                    selectedPlaylist = playlist
                    val firstSongTitle = playlist.songs.firstOrNull()?.title
                    currentSong = allSongs.firstOrNull { it.title == firstSongTitle }
                    currentSong?.let { musicViewModel.playSong(it) }
                    isPlaying = true
                    currentView = PlaylistView.NowPlaying
                },
                onCreatePlaylist = {
                    currentView = PlaylistView.Create
                }
            )
        }
        is PlaylistView.Create -> {
            CreatePlaylistContent(
                onBack = { currentView = PlaylistView.List },
                onCreate = { name, description ->
                    val newPlaylist = Playlist(
                        id = (playlists.maxOfOrNull { it.id } ?: 0) + 1,
                        name = name,
                        description = description,
                        songCount = 0,
                        songs = emptyList(),
                        isAiGenerated = false
                    )
                    userPlaylists = userPlaylists + newPlaylist
                    currentView = PlaylistView.List
                }
            )
        }
        is PlaylistView.AudioSettings -> {
            AudioSettingsContent(
                bassLevel = bassLevel,
                trebleLevel = trebleLevel,
                volumeLevel = volumeLevel,
                onBassChange = { bassLevel = it },
                onTrebleChange = { trebleLevel = it },
                onVolumeChange = { volumeLevel = it },
                onBack = { currentView = PlaylistView.NowPlaying }
            )
        }
        is PlaylistView.Detail -> {
            selectedPlaylist?.let { playlist ->
                PlaylistDetailContent(
                    playlist = playlist,
                    allSongs = allSongs,
                    onBack = { currentView = PlaylistView.List },
                    onSongClick = { playlistSong ->
                        val song = allSongs.firstOrNull { it.title == playlistSong.title }
                        currentSong = song
                        song?.let { musicViewModel.playSong(it) }
                        isPlaying = true
                        currentView = PlaylistView.NowPlaying
                    },
                    onPlayAll = {
                        val firstSongTitle = playlist.songs.firstOrNull()?.title
                        currentSong = allSongs.firstOrNull { it.title == firstSongTitle }
                        currentSong?.let { musicViewModel.playSong(it) }
                        isPlaying = true
                        currentView = PlaylistView.NowPlaying
                    }
                )
            }
        }
        is PlaylistView.NowPlaying -> {
            currentSong?.let { song ->
                NowPlayingContentFirebase(
                    song = song,
                    playlist = selectedPlaylist,
                    isPlaying = isPlaying,
                    onPlayPauseClick = {
                        isPlaying = !isPlaying
                        if (isPlaying) musicViewModel.resume() else musicViewModel.pause()
                    },
                    onBack = { currentView = PlaylistView.Detail },
                    onNext = {
                        musicViewModel.skipToNext()
                    },
                    onPrevious = {
                        musicViewModel.skipToPrevious()
                    },
                    onOpenSettings = { currentView = PlaylistView.AudioSettings }
                )
            }
        }
    }
}

sealed class PlaylistView {
    object List : PlaylistView()
    object Detail : PlaylistView()
    object NowPlaying : PlaylistView()
    object Create : PlaylistView()
    object AudioSettings : PlaylistView()
}

@Composable
fun PlaylistListContent(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onQuickPlay: (Playlist) -> Unit,
    onCreatePlaylist: () -> Unit
) {
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
                onClick = onCreatePlaylist,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB040FF)),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Create", fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "✨ AI Smart Playlists",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            aiPlaylists.forEach { playlist ->
                AiPlaylistCardItem(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) },
                    onPlayClick = { onQuickPlay(playlist) }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "🎵 Your Playlists",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(Modifier.height(16.dp))

        userPlaylists.forEach { playlist ->
            UserPlaylistRowItem(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist) }
            )
        }

        Spacer(Modifier.height(32.dp))

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
            QuickCreateCardItem(
                title = "Liked Songs",
                subtitle = "Auto playlist",
                icon = Icons.Default.Favorite,
                gradient = Brush.horizontalGradient(
                    listOf(Color(0xFFE91E63), Color(0xFF9C27B0))
                ),
                modifier = Modifier.weight(1f),
                onClick = {}
            )
            QuickCreateCardItem(
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
fun AiPlaylistCardItem(
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB040FF)),
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
fun UserPlaylistRowItem(
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
fun QuickCreateCardItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
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

@Composable
fun CreatePlaylistContent(
    onBack: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    var playlistDescription by remember { mutableStateOf("") }

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Create Playlist",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFB040FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text = "Playlist Name",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = playlistName,
            onValueChange = { playlistName = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter playlist name", color = Color.White.copy(0.5f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFB040FF),
                unfocusedBorderColor = Color.White.copy(0.3f),
                cursorColor = Color(0xFFB040FF)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Description",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = playlistDescription,
            onValueChange = { playlistDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Describe your playlist", color = Color.White.copy(0.5f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFB040FF),
                unfocusedBorderColor = Color.White.copy(0.3f),
                cursorColor = Color(0xFFB040FF)
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                if (playlistName.isNotBlank()) {
                    onCreate(playlistName, playlistDescription)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB040FF),
                disabledContainerColor = Color(0xFFB040FF).copy(0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = playlistName.isNotBlank()
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Create Playlist",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "💡 Tips",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "• Give your playlist a catchy name\n" +
                            "• Add a description to help you remember\n" +
                            "• You can add songs later\n" +
                            "• Share with friends when ready",
                    fontSize = 14.sp,
                    color = Color.White.copy(0.8f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun AudioSettingsContent(
    bassLevel: Float,
    trebleLevel: Float,
    volumeLevel: Float,
    onBassChange: (Float) -> Unit,
    onTrebleChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onBack: () -> Unit
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
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Audio Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(Color(0xFFB040FF).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.GraphicEq,
                contentDescription = null,
                tint = Color(0xFFB040FF),
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(Modifier.height(40.dp))

        AudioControlCardItem(
            title = "Volume",
            icon = Icons.Default.VolumeUp,
            value = volumeLevel,
            onValueChange = onVolumeChange,
            valueText = "${(volumeLevel * 100).toInt()}%"
        )

        Spacer(Modifier.height(24.dp))

        AudioControlCardItem(
            title = "Bass",
            icon = Icons.Default.MusicNote,
            value = (bassLevel + 10f) / 20f,
            onValueChange = { onBassChange(it * 20f - 10f) },
            valueText = "${bassLevel.toInt()} dB",
            minLabel = "-10",
            maxLabel = "+10"
        )

        Spacer(Modifier.height(24.dp))

        AudioControlCardItem(
            title = "Treble",
            icon = Icons.Default.TrendingUp,
            value = (trebleLevel + 10f) / 20f,
            onValueChange = { onTrebleChange(it * 20f - 10f) },
            valueText = "${trebleLevel.toInt()} dB",
            minLabel = "-10",
            maxLabel = "+10"
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Presets",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PresetButtonItem(
                text = "Flat",
                modifier = Modifier.weight(1f),
                onClick = {
                    onBassChange(0f)
                    onTrebleChange(0f)
                }
            )
            PresetButtonItem(
                text = "Bass Boost",
                modifier = Modifier.weight(1f),
                onClick = {
                    onBassChange(6f)
                    onTrebleChange(-2f)
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PresetButtonItem(
                text = "Treble Boost",
                modifier = Modifier.weight(1f),
                onClick = {
                    onBassChange(-2f)
                    onTrebleChange(6f)
                }
            )
            PresetButtonItem(
                text = "Rock",
                modifier = Modifier.weight(1f),
                onClick = {
                    onBassChange(5f)
                    onTrebleChange(3f)
                }
            )
        }
    }
}

@Composable
fun AudioControlCardItem(
    title: String,
    icon: ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueText: String,
    minLabel: String = "0",
    maxLabel: String = "100"
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color(0xFFB040FF),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = valueText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB040FF)
                )
            }

            Spacer(Modifier.height(20.dp))

            Slider(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFB040FF),
                    activeTrackColor = Color(0xFFB040FF),
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = minLabel,
                    fontSize = 12.sp,
                    color = Color.White.copy(0.6f)
                )
                Text(
                    text = maxLabel,
                    fontSize = 12.sp,
                    color = Color.White.copy(0.6f)
                )
            }
        }
    }
}

@Composable
fun PresetButtonItem(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1B4E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun PlaylistDetailContent(
    playlist: Playlist,
    allSongs: List<Song>,
    onBack: () -> Unit,
    onSongClick: (PlaylistSong) -> Unit,
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

        val firstSongInPlaylist = allSongs.firstOrNull { song ->
            playlist.songs.any { it.title == song.title }
        }

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF7FC9BC)),
            contentAlignment = Alignment.Center
        ) {
            if (firstSongInPlaylist?.coverUrl?.isNotEmpty() == true) {
                AsyncImage(
                    model = firstSongInPlaylist.coverUrl,
                    contentDescription = playlist.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPlayAll,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5DBDA6)),
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

        playlist.songs.forEachIndexed { index, song ->
            PlaylistSongItem(
                index = index + 1,
                song = song,
                onClick = { onSongClick(song) }
            )
        }
    }
}

@Composable
fun PlaylistSongItem(
    index: Int,
    song: PlaylistSong,
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

@Composable
fun NowPlayingContent(
    song: PlaylistSong,
    playlist: Playlist?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onOpenSettings: () -> Unit
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

            IconButton(onClick = onOpenSettings) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Audio Settings",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

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

@Composable
fun NowPlayingContentFirebase(
    song: Song,
    playlist: Playlist?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onOpenSettings: () -> Unit
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

            IconButton(onClick = onOpenSettings) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Audio Settings",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFB040FF)),
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
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

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
                Text("0:00", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text(song.durationFormatted, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(40.dp))

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

// Sample data
fun getPlaylistData(): List<Playlist> {
    return listOf(
        Playlist(
            id = 1,
            name = "Focus Flow",
            description = "AI curated for productivity",
            songCount = 32,
            isAiGenerated = true,
            songs = listOf(
                PlaylistSong(1, "Deep Focus", "Ambient Collective", "4:32"),
                PlaylistSong(2, "Concentration Mode", "Study Beats", "3:45"),
                PlaylistSong(3, "Mind Flow", "Zen Masters", "5:12"),
                PlaylistSong(4, "Brain Waves", "Focus Music", "4:20")
            )
        ),
        Playlist(
            id = 2,
            name = "Evening Calm",
            description = "Relaxing evening vibes",
            songCount = 15,
            isAiGenerated = true,
            songs = listOf(
                PlaylistSong(5, "Sunset Dreams", "Chill Artists", "3:28"),
                PlaylistSong(6, "Evening Breeze", "Smooth Sounds", "4:15"),
                PlaylistSong(7, "Twilight Hour", "Relaxation Zone", "5:03")
            )
        ),
        Playlist(
            id = 3,
            name = "Chill Vibes",
            description = "Relaxing tunes for any time",
            songCount = 24,
            isAiGenerated = false,
            songs = listOf(
                PlaylistSong(8, "kissme", "Red Love", "3:12"),
                PlaylistSong(9, "radio", "Lana Del Rey", "4:28"),
                PlaylistSong(10, "Face", "Larosea", "3:45"),
                PlaylistSong(11, "Moonlight", "Indie Dreams", "4:02")
            )
        ),
        Playlist(
            id = 4,
            name = "Workout Energy",
            description = "High energy beats",
            songCount = 18,
            isAiGenerated = false,
            songs = listOf(
                PlaylistSong(12, "Power Up", "Gym Beats", "3:30"),
                PlaylistSong(13, "Maximum Drive", "Workout Mix", "3:55"),
                PlaylistSong(14, "Beast Mode", "Fitness Music", "4:10")
            )
        )
    )
}