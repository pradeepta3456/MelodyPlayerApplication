package com.example.musicplayerapplication.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayerapplication.ViewModel.MusicViewModel
import com.example.musicplayerapplication.ViewModel.MusicViewModelFactory
import com.example.musicplayerapplication.ViewModel.SavedViewModel
import com.example.musicplayerapplication.ViewModel.SavedViewModelFactory
import com.example.musicplayerapplication.View.components.StandardSongCard
import com.example.musicplayerapplication.model.Song

class ArtistActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val artistName = intent.getStringExtra("ARTIST_NAME").orEmpty()
        setContent {
            ArtistScreen(artistName = artistName)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    artistName: String
) {
    val context = LocalContext.current
    val musicViewModel: MusicViewModel = viewModel(factory = MusicViewModelFactory(context))
    val savedViewModel: SavedViewModel = viewModel(factory = SavedViewModelFactory())
    val songsState by musicViewModel.allSongs.collectAsState()
    val savedSongs by savedViewModel.savedSongs.collectAsState()

    // Filter songs for this artist; repository already exposes getSongsByArtist,
    // but we can also reuse the in-memory list for responsiveness.
    val artistSongs = songsState.filter { it.artist.equals(artistName, ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF21133B))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = artistName.ifEmpty { "Artist" },
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${artistSongs.size} songs",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(artistSongs) { song ->
                    val isFavorite = savedSongs.any { it.id == song.id }
                    StandardSongCard(
                        song = song,
                        isFavorite = isFavorite,
                        onSongClick = { musicViewModel.playSong(song) },
                        onPlayClick = { musicViewModel.playSong(song) },
                        onSaveClick = { savedViewModel.toggleFavorite(song) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistSongRow(
    song: Song,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D1B4E)
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF6B4FA0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = song.album.takeIf { it.isNotBlank() } ?: "Single",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            Text(
                text = song.durationFormatted,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}