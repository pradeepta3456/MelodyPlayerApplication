package com.example.musicplayerapplication.View

import android.content.Context
import android.widget.Toast
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
import com.example.musicplayerapplication.ViewModel.AudioEffectsViewModel
import com.example.musicplayerapplication.ViewModel.AudioEffectsViewModelFactory
import com.example.musicplayerapplication.ViewModel.SavedViewModel
import com.example.musicplayerapplication.ViewModel.SavedViewModelFactory
import com.example.musicplayerapplication.repository.PlaylistRepoImpl
import com.example.musicplayerapplication.model.UserPlaylist
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.LazyColumn
import kotlinx.coroutines.launch

// Data classes (local to this file since they're specific to playlist feature)
data class PlaylistSong(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: String,
    val songId: String = ""  // Firebase song ID for accurate matching
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
fun PlaylistScreen(musicViewModel: MusicViewModel) {
    val context = LocalContext.current
    val allSongs by musicViewModel.allSongs.collectAsState()

    var currentView by remember { mutableStateOf<PlaylistView>(PlaylistView.List) }
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // User-created playlists
    var userPlaylists by remember { mutableStateOf<List<Playlist>>(emptyList()) }

    // Firebase user playlists
    val playlistRepo = remember { PlaylistRepoImpl() }
    val auth = FirebaseAuth.getInstance()
    var firebaseUserPlaylists by remember { mutableStateOf<List<UserPlaylist>>(emptyList()) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var selectedPlaylistForSongs by remember { mutableStateOf<UserPlaylist?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load user playlists from Firebase
    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let { userId ->
            playlistRepo.getUserPlaylists(userId).onSuccess { playlists ->
                firebaseUserPlaylists = playlists
            }
        }
    }

    // Saved ViewModel for favorites playlist
    val savedViewModel: SavedViewModel = viewModel(factory = SavedViewModelFactory())
    val savedSongs by savedViewModel.savedSongs.collectAsState()

    // Convert Firebase songs to playlists
    val playlists by remember(allSongs, savedSongs) {
        derivedStateOf {
            val firebasePlaylists = mutableListOf<Playlist>()

            // Create Favorites playlist FIRST (appears at top)
            if (savedSongs.isNotEmpty()) {
                firebasePlaylists.add(
                    Playlist(
                        id = 0, // Special ID for favorites
                        name = "❤️ My Favorites",
                        description = "${savedSongs.size} favorite ${if (savedSongs.size == 1) "song" else "songs"}",
                        songCount = savedSongs.size,
                        songs = savedSongs.map { song ->
                            PlaylistSong(
                                id = song.id.hashCode(),
                                title = song.title,
                                artist = song.artist,
                                duration = song.durationFormatted,
                                songId = song.id
                            )
                        },
                        isAiGenerated = false
                    )
                )
            }

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
                                    duration = song.durationFormatted,
                                    songId = song.id  // Store Firebase ID for accurate matching
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
                                    duration = song.durationFormatted,
                                    songId = song.id  // Store Firebase ID for accurate matching
                                )
                            },
                            isAiGenerated = false
                        )
                    )
                }
            }

            firebasePlaylists + userPlaylists
        }
    }

    // Audio effects ViewModel for actual audio control
    val audioEffectsViewModel: AudioEffectsViewModel = viewModel(
        factory = AudioEffectsViewModelFactory(context)
    )
    val bassLevel by audioEffectsViewModel.bassLevel.collectAsState()
    val trebleLevel by audioEffectsViewModel.trebleLevel.collectAsState()
    val volumeLevel by audioEffectsViewModel.volumeLevel.collectAsState()

    when (currentView) {
        is PlaylistView.List -> {
            PlaylistListContent(
                playlists = playlists,
                firebaseUserPlaylists = firebaseUserPlaylists,
                allSongs = allSongs,
                showCreatePlaylistDialog = showCreatePlaylistDialog,
                selectedPlaylistForSongs = selectedPlaylistForSongs,
                auth = auth,
                playlistRepo = playlistRepo,
                coroutineScope = coroutineScope,
                onShowCreatePlaylistDialogChange = { showCreatePlaylistDialog = it },
                onSelectedPlaylistForSongsChange = { selectedPlaylistForSongs = it },
                onFirebaseUserPlaylistsChange = { firebaseUserPlaylists = it },
                onSelectedPlaylistChange = { selectedPlaylist = it },
                onCurrentViewChange = { currentView = it },
                onPlaylistClick = { playlist ->
                    selectedPlaylist = playlist
                    currentView = PlaylistView.Detail
                },
                onQuickPlay = { playlist ->
                    selectedPlaylist = playlist
                    // Get all songs in this playlist
                    val playlistSongs = playlist.songs.mapNotNull { ps ->
                        allSongs.firstOrNull { song ->
                            if (ps.songId.isNotEmpty()) {
                                song.id == ps.songId
                            } else {
                                song.title == ps.title && song.artist == ps.artist
                            }
                        }
                    }

                    if (playlistSongs.isNotEmpty()) {
                        musicViewModel.setPlaylist(playlistSongs)
                        currentSong = playlistSongs.first()
                        musicViewModel.playSong(playlistSongs.first())
                        isPlaying = true
                        currentView = PlaylistView.NowPlaying
                    }
                },
                onPlayUserPlaylist = { userPlaylist ->
                    // Get all songs from the user playlist by matching song IDs
                    val playlistSongs = userPlaylist.songIds.mapNotNull { songId ->
                        allSongs.firstOrNull { song -> song.id == songId }
                    }

                    if (playlistSongs.isNotEmpty()) {
                        musicViewModel.setPlaylist(playlistSongs)
                        currentSong = playlistSongs.first()
                        musicViewModel.playSong(playlistSongs.first())
                        isPlaying = true
                        currentView = PlaylistView.NowPlaying
                    }
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
                onBassChange = { audioEffectsViewModel.updateBassLevel(it) },
                onTrebleChange = { audioEffectsViewModel.updateTrebleLevel(it) },
                onVolumeChange = { audioEffectsViewModel.updateVolumeLevel(it) },
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
                        // Get all songs in this playlist
                        val playlistSongs = playlist.songs.mapNotNull { ps ->
                            allSongs.firstOrNull { song ->
                                if (ps.songId.isNotEmpty()) {
                                    song.id == ps.songId
                                } else {
                                    song.title == ps.title && song.artist == ps.artist
                                }
                            }
                        }
                        val song = allSongs.firstOrNull { song ->
                            if (playlistSong.songId.isNotEmpty()) {
                                song.id == playlistSong.songId
                            } else {
                                song.title == playlistSong.title && song.artist == playlistSong.artist
                            }
                        }
                        currentSong = song

                        // Set the playlist so next/previous works
                        if (playlistSongs.isNotEmpty()) {
                            musicViewModel.setPlaylist(playlistSongs)
                        }

                        song?.let { musicViewModel.playSong(it) }
                        isPlaying = true
                        currentView = PlaylistView.NowPlaying
                    },
                    onPlayAll = {
                        // Get all songs in this playlist
                        val playlistSongs = playlist.songs.mapNotNull { ps ->
                            allSongs.firstOrNull { song ->
                                if (ps.songId.isNotEmpty()) {
                                    song.id == ps.songId
                                } else {
                                    song.title == ps.title && song.artist == ps.artist
                                }
                            }
                        }

                        if (playlistSongs.isNotEmpty()) {
                            musicViewModel.setPlaylist(playlistSongs)
                            currentSong = playlistSongs.first()
                            musicViewModel.playSong(playlistSongs.first())
                            isPlaying = true
                            currentView = PlaylistView.NowPlaying
                        }
                    }
                )
            }
        }
        is PlaylistView.NowPlaying -> {
            currentSong?.let { song ->
                val playbackState by musicViewModel.playbackState.collectAsState()

                NowPlayingScreen(
                    song = song,
                    isPlaying = isPlaying,
                    currentPosition = playbackState.currentPosition,
                    duration = song.duration,
                    shuffleEnabled = playbackState.isShuffleEnabled,
                    repeatMode = playbackState.repeatMode,
                    onPlayPauseClick = {
                        isPlaying = !isPlaying
                        if (isPlaying) musicViewModel.resume() else musicViewModel.pause()
                    },
                    onBackClick = { currentView = PlaylistView.Detail },
                    onSkipNext = {
                        musicViewModel.skipToNext()
                    },
                    onSkipPrevious = {
                        musicViewModel.skipToPrevious()
                    },
                    onToggleShuffle = {
                        musicViewModel.toggleShuffle()
                    },
                    onToggleRepeat = {
                        musicViewModel.toggleRepeatMode()
                    },
                    onToggleFavorite = {
                        musicViewModel.toggleFavorite(song)
                    },
                    onSeekTo = { position ->
                        musicViewModel.seekTo(position)
                    },
                    onAudioEffectsClick = { currentView = PlaylistView.AudioSettings }
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
    firebaseUserPlaylists: List<UserPlaylist>,
    allSongs: List<Song>,
    showCreatePlaylistDialog: Boolean,
    selectedPlaylistForSongs: UserPlaylist?,
    auth: FirebaseAuth,
    playlistRepo: PlaylistRepoImpl,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onShowCreatePlaylistDialogChange: (Boolean) -> Unit,
    onSelectedPlaylistForSongsChange: (UserPlaylist?) -> Unit,
    onFirebaseUserPlaylistsChange: (List<UserPlaylist>) -> Unit,
    onSelectedPlaylistChange: (Playlist?) -> Unit,
    onCurrentViewChange: (PlaylistView) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onQuickPlay: (Playlist) -> Unit,
    onPlayUserPlaylist: (UserPlaylist) -> Unit,
    onCreatePlaylist: () -> Unit
) {
    val aiPlaylists = playlists.filter { it.isAiGenerated }
    val userPlaylists = playlists.filter { !it.isAiGenerated }

    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF0A0E27),
            Color(0xFF1E293B),
            Color(0xFF334155)
        )
    )

    Column(
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
        // Header with Create Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Your Library",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${firebaseUserPlaylists.size + playlists.size} playlists",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
            }
            FloatingActionButton(
                onClick = { onShowCreatePlaylistDialogChange(true) },
                containerColor = Color(0xFF818CF8),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Create Playlist")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Created Playlists
            if (firebaseUserPlaylists.isNotEmpty()) {
                item {
                    Text(
                        "MY PLAYLISTS",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(firebaseUserPlaylists.size) { index ->
                    val userPlaylist = firebaseUserPlaylists[index]
                    UserPlaylistCard(
                        playlist = userPlaylist,
                        allSongs = allSongs,
                        onClick = { onSelectedPlaylistForSongsChange(userPlaylist) },
                        onPlay = { onPlayUserPlaylist(userPlaylist) },
                        onDelete = {
                            coroutineScope.launch {
                                auth.currentUser?.uid?.let { userId ->
                                    playlistRepo.deleteUserPlaylist(userId, userPlaylist.id).onSuccess {
                                        onFirebaseUserPlaylistsChange(firebaseUserPlaylists.filter { it.id != userPlaylist.id })
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Auto-Generated Playlists
            item {
                Text(
                    "SMART PLAYLISTS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
            }
            items(playlists.size) { index ->
                PlaylistCard(
                    playlist = playlists[index],
                    onClick = {
                        onSelectedPlaylistChange(playlists[index])
                        onCurrentViewChange(PlaylistView.Detail)
                    }
                )
            }
        }
    }

    // Create Playlist Dialog
    if (showCreatePlaylistDialog) {
        val context = LocalContext.current
        CreatePlaylistDialog(
            allSongs = allSongs,
            onDismiss = { onShowCreatePlaylistDialogChange(false) },
            onCreate = { name, description, selectedSongs ->
                coroutineScope.launch {
                    val userId = auth.currentUser?.uid
                    if (userId == null) {
                        Toast.makeText(context, "Please sign in to create a playlist", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    val songIds = selectedSongs.map { it.id }
                    playlistRepo.createUserPlaylist(userId, name, description, songIds)
                        .onSuccess { newPlaylist ->
                            onFirebaseUserPlaylistsChange(listOf(newPlaylist) + firebaseUserPlaylists)
                            onShowCreatePlaylistDialogChange(false)
                            Toast.makeText(context, "Playlist created successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .onFailure { error ->
                            onShowCreatePlaylistDialogChange(false)
                            Toast.makeText(context, "Failed to create playlist: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
        )
    }

    // Manage Playlist Songs Dialog
    selectedPlaylistForSongs?.let { playlist ->
        ManagePlaylistSongsDialog(
            playlist = playlist,
            allSongs = allSongs,
            onDismiss = {
                onSelectedPlaylistForSongsChange(null)
                // Reload playlists
                coroutineScope.launch {
                    auth.currentUser?.uid?.let { userId ->
                        playlistRepo.getUserPlaylists(userId).onSuccess { playlists ->
                            onFirebaseUserPlaylistsChange(playlists)
                        }
                    }
                }
            },
            onAddSong = { songId ->
                coroutineScope.launch {
                    auth.currentUser?.uid?.let { userId ->
                        playlistRepo.addSongToUserPlaylist(userId, playlist.id, songId)
                    }
                }
            },
            onRemoveSong = { songId ->
                coroutineScope.launch {
                    auth.currentUser?.uid?.let { userId ->
                        playlistRepo.removeSongFromUserPlaylist(userId, playlist.id, songId)
                    }
                }
            }
        )
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF818CF8)),
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
            .background(Color(0xFF1E293B))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF818CF8)),
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
            Color(0xFF1E293B),
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
                .background(Color(0xFF818CF8)),
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
                focusedBorderColor = Color(0xFF818CF8),
                unfocusedBorderColor = Color.White.copy(0.3f),
                cursorColor = Color(0xFF818CF8)
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
                focusedBorderColor = Color(0xFF818CF8),
                unfocusedBorderColor = Color.White.copy(0.3f),
                cursorColor = Color(0xFF818CF8)
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
                containerColor = Color(0xFF818CF8),
                disabledContainerColor = Color(0xFF818CF8).copy(0.5f)
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
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
            Color(0xFF1E293B),
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
                .background(Color(0xFF818CF8).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.GraphicEq,
                contentDescription = null,
                tint = Color(0xFF818CF8),
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
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
                        tint = Color(0xFF818CF8),
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
                    color = Color(0xFF818CF8)
                )
            }

            Spacer(Modifier.height(20.dp))

            Slider(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF818CF8),
                    activeTrackColor = Color(0xFF818CF8),
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
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
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
            Color(0xFF1E293B),
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
                .background(Color(0xFF818CF8)),
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
                    activeTrackColor = Color(0xFF818CF8),
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
                containerColor = Color(0xFF818CF8),
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
    shuffleEnabled: Boolean = false,
    repeatMode: com.example.musicplayerapplication.model.RepeatMode = com.example.musicplayerapplication.model.RepeatMode.OFF,
    currentPosition: Long = 0L,
    duration: Long = 0L,
    onPlayPauseClick: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit = {},
    onToggleRepeat: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    onSeekTo: (Long) -> Unit = {},
    onOpenSettings: () -> Unit
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1A0B2E),
            Color(0xFF1E293B),
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
                .background(Color(0xFF818CF8)),
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

        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { onSeekTo(it.toLong()) },
                valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF818CF8),
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(currentPosition), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text(formatTime(duration), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleShuffle) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (shuffleEnabled) Color(0xFF818CF8) else Color.White.copy(alpha = 0.7f),
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
                containerColor = Color(0xFF818CF8),
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

            IconButton(onClick = onToggleRepeat) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = when(repeatMode) {
                        com.example.musicplayerapplication.model.RepeatMode.OFF -> Color.White.copy(alpha = 0.7f)
                        else -> Color(0xFF818CF8)
                    },
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (song.isFavorite) Color(0xFFE91E63) else Color.White,
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

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    playlist.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${playlist.songCount} songs",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun UserPlaylistCard(
    playlist: UserPlaylist,
    allSongs: List<Song>,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val hasSongs = playlist.songIds.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play button overlay on the album art
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF818CF8))
                    .clickable(enabled = hasSongs) { onPlay() },
                contentAlignment = Alignment.Center
            ) {
                if (hasSongs) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.LibraryMusic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    playlist.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${playlist.songIds.size} songs",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            // Edit button
            IconButton(onClick = { onClick() }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF818CF8)
                )
            }

            // Delete button
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Playlist?", color = Color.White) },
            text = { Text("Are you sure you want to delete \"${playlist.name}\"?", color = Color.White.copy(0.9f)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
    allSongs: List<Song>,
    onDismiss: () -> Unit,
    onCreate: (String, String, List<Song>) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    var playlistDescription by remember { mutableStateOf("") }
    var selectedSongs by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

    val filteredSongs = allSongs.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.artist.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    "Create Playlist",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Playlist Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = Color(0xFF818CF8),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = playlistDescription,
                    onValueChange = { playlistDescription = it },
                    label = { Text("Description (optional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = Color(0xFF818CF8),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Add Songs (${selectedSongs.size} selected)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search songs...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSongs.size) { index ->
                        val song = filteredSongs[index]
                        val isSelected = selectedSongs.contains(song.id)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF3D2766) else Color.Transparent)
                                .clickable {
                                    selectedSongs = if (isSelected) {
                                        selectedSongs - song.id
                                    } else {
                                        selectedSongs + song.id
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    selectedSongs = if (it) {
                                        selectedSongs + song.id
                                    } else {
                                        selectedSongs - song.id
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF818CF8),
                                    uncheckedColor = Color.White.copy(alpha = 0.5f)
                                )
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    song.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    song.artist,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isCreating,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = if (isCreating) Color.Gray else Color.White)
                    }

                    Button(
                        onClick = {
                            if (playlistName.isNotBlank()) {
                                isCreating = true
                                val selected = allSongs.filter { selectedSongs.contains(it.id) }
                                onCreate(playlistName, playlistDescription, selected)
                            }
                        },
                        enabled = playlistName.isNotBlank() && !isCreating,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF818CF8)),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePlaylistSongsDialog(
    playlist: UserPlaylist,
    allSongs: List<Song>,
    onDismiss: () -> Unit,
    onAddSong: (String) -> Unit,
    onRemoveSong: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val playlistSongs = allSongs.filter { playlist.songIds.contains(it.id) }
    val availableSongs = allSongs.filter { !playlist.songIds.contains(it.id) }

    val filteredAvailable = availableSongs.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.artist.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            playlist.name,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${playlist.songIds.size} songs",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Current Songs
                if (playlistSongs.isNotEmpty()) {
                    Text(
                        "CURRENT SONGS",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    playlistSongs.take(3).forEach { song ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(song.title, color = Color.White, fontSize = 14.sp)
                                Text(song.artist, color = Color.White.copy(0.7f), fontSize = 12.sp)
                            }

                            IconButton(onClick = { onRemoveSong(song.id) }) {
                                Icon(Icons.Default.Remove, "Remove", tint = Color(0xFFEF4444))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Add Songs
                Text(
                    "ADD SONGS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search songs...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAvailable.size) { index ->
                        val song = filteredAvailable[index]

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF3D1F5C))
                                .clickable { onAddSong(song.id) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    song.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    song.artist,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }

                            Icon(
                                Icons.Default.Add,
                                "Add",
                                tint = Color(0xFF818CF8)
                            )
                        }
                    }
                }
            }
        }
    }
}
