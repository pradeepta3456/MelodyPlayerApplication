package com.example.musicplayerapplication.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.LibraryRepoImpl
import com.example.musicplayerapplication.ViewModel.LibraryViewModel

// Main Library Screen - Use this in DashboardActivity
@Composable
fun LibraryScreen() {
    val viewModel = remember { LibraryViewModel(repository = LibraryRepoImpl()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Albums") }
    var selectedArtist by remember { mutableStateOf<String?>(null) }

    val artists = viewModel.artists
    val categories = listOf(
        "Songs" to R.drawable.baseline_music_note_24,
        "Albums" to R.drawable.baseline_album_24,
        "Artists" to R.drawable.baseline_person_24,
        "Genres" to R.drawable.baseline_library_music_24,
        "Folders" to R.drawable.baseline_folder_open_24
    )

    // Show album detail if artist is selected
    if (selectedArtist != null) {
        AlbumDetailScreenContent(
            albumName = selectedArtist!!,
            onBack = { selectedArtist = null }
        )
    } else {
        // Main library view
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6176E3))
        ) {
            // Search Bar and Categories
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF414C91))
                    .padding(16.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search artists, songs, albums",
                            color = Color(0xFFB0B0B0)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_menu_24),
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2C3883),
                        unfocusedContainerColor = Color(0xFF272F72),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (label, iconRes) ->
                        LibraryCategoryChip(
                            label = label,
                            iconResId = iconRes,
                            selected = label == selectedCategory
                        ) {
                            selectedCategory = label
                        }
                    }
                }
            }

            // Content based on selected category
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2C3C72))
            ) {
                when (selectedCategory) {
                    "Albums" -> LibraryArtistList(
                        artists = artists,
                        onArtistClick = { artist -> selectedArtist = artist.name }
                    )
                    else -> SimpleCategoryScreen(selectedCategory)
                }
            }
        }
    }
}

@Composable
fun LibraryArtistList(
    artists: List<LibraryArtist>,
    onArtistClick: (LibraryArtist) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(artists) { artist ->
            LibraryArtistCard(artist = artist, onClick = { onArtistClick(artist) })
        }
    }
}

@Composable
fun LibraryArtistCard(artist: LibraryArtist, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF4A5A8A)),
            contentAlignment = Alignment.Center
        ) {
            if (artist.imageResId != null) {
                Image(
                    painter = painterResource(id = artist.imageResId),
                    contentDescription = artist.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = artist.name,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = artist.name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "${artist.songCount} Songs, ${artist.albumCount} Albums",
            color = Color(0xFFB0B0B0),
            fontSize = 13.sp
        )
    }
}

@Composable
fun LibraryCategoryChip(label: String, iconResId: Int, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF9199B4),
            selectedLabelColor = Color.White,
            containerColor = Color(0xFF596791),
            labelColor = Color.White
        )
    )
}

@Composable
fun AlbumDetailScreenContent(albumName: String, onBack: () -> Unit) {
    val songs = remember(albumName) { getSongsForArtist(albumName) }
    val durations = remember(albumName) { getDurationsForArtist(albumName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3C72))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF414C91))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .clickable { onBack() }
                    .size(24.dp)
            )
            Text(
                text = albumName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Songs List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(songs.size) { index ->
                LibraryAlbumSongItem(
                    song = songs[index],
                    duration = durations[index]
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                LibrarySignInPromoCard()
            }
        }
    }
}

@Composable
fun LibraryAlbumSongItem(song: Song, duration: String) {
    var isFavorite by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A4A7A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = song.cover),
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = song.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(
                    text = duration,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            IconButton(onClick = { isFavorite = !isFavorite }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFEC4899) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = { isPlaying = !isPlaying }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun LibrarySignInPromoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A3A6A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sign in",
                        color = Color(0xFF2C3C72),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color(0xFF2C3C72),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "Create more Playlist and Customize your music",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SimpleCategoryScreen(category: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$category Screen - Coming Soon",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Helper functions
fun getSongsForArtist(artistName: String): List<Song> {
    return when (artistName) {
        "Luna Eclipse" -> listOf(
            Song(1, "Katsee", "Artist", R.drawable.img_1, 0),
            Song(2, "Miline", "Artist", R.drawable.img_5, 0),
            Song(3, "Star", "Artist", R.drawable.img_6, 0),
            Song(4, "Eclipse", "Artist", R.drawable.img_7, 0),
            Song(5, "Moon", "Artist", R.drawable.img_10, 0)
        )
        "Sunshine" -> listOf(
            Song(1, "Lily", "Artist", R.drawable.img_2, 0),
            Song(2, "Bright", "Artist", R.drawable.img_11, 0),
            Song(3, "Golden", "Artist", R.drawable.img_12, 0),
            Song(4, "Summer", "Artist", R.drawable.img14, 0),
            Song(5, "Radiant", "Artist", R.drawable.img15, 0)
        )
        "Poster Girl" -> listOf(
            Song(1, "Casitia", "Artist", R.drawable.img_3, 0),
            Song(2, "Fashion", "Artist", R.drawable.img_4, 0),
            Song(3, "Style", "Artist", R.drawable.img_5, 0),
            Song(4, "Glam", "Artist", R.drawable.img_6, 0),
            Song(5, "Trend", "Artist", R.drawable.img_7, 0)
        )
        "Disco Drive" -> listOf(
            Song(1, "Danielle", "Artist", R.drawable.img_4, 0),
            Song(2, "Risern", "Artist", R.drawable.img_5, 0),
            Song(3, "Night", "Artist", R.drawable.img_6, 0),
            Song(4, "Dance", "Artist", R.drawable.img_7, 0),
            Song(5, "Groove", "Artist", R.drawable.img_10, 0)
        )
        else -> listOf(
            Song(1, "Song 1", "Artist", R.drawable.img_1, 0),
            Song(2, "Song 2", "Artist", R.drawable.img_2, 0),
            Song(3, "Song 3", "Artist", R.drawable.img_3, 0),
            Song(4, "Song 4", "Artist", R.drawable.img_4, 0),
            Song(5, "Song 5", "Artist", R.drawable.img_5, 0)
        )
    }
}

fun getDurationsForArtist(artistName: String): List<String> {
    return when (artistName) {
        "Luna Eclipse" -> listOf("2:50", "3:15", "4:20", "3:45", "5:10")
        "Sunshine" -> listOf("3:05", "2:30", "3:55", "4:10", "3:20")
        "Poster Girl" -> listOf("3:54", "2:45", "3:30", "4:05", "3:15")
        "Disco Drive" -> listOf("2:45", "5:12", "4:30", "3:25", "4:50")
        else -> listOf("2:50", "3:05", "3:54", "2:45", "5:12")
    }
}