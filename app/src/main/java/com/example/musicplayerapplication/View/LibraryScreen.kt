package com.example.musicplayerapplication.View

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.LibraryRepoImpl
import com.example.musicplayerapplication.ViewModel.LibraryViewModel
import com.example.musicplayerapplication.ViewModel.MusicViewModel
import com.example.musicplayerapplication.ViewModel.MusicViewModelFactory
import com.example.musicplayerapplication.ViewModel.SavedViewModel
import com.example.musicplayerapplication.ViewModel.SavedViewModelFactory
import com.example.musicplayerapplication.View.components.StandardSongCard

// Main Library Screen
@Composable
fun LibraryScreen(musicViewModel: MusicViewModel) {
    val context = LocalContext.current
    val viewModel = remember { LibraryViewModel(repository = LibraryRepoImpl()) }
    val savedViewModel: SavedViewModel = viewModel(factory = SavedViewModelFactory())
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Songs") }
    var selectedAlbum by remember { mutableStateOf<String?>(null) }
    var selectedGenre by remember { mutableStateOf<String?>(null) }

    val allSongs by musicViewModel.allSongs.collectAsState()
    val savedSongs by savedViewModel.savedSongs.collectAsState()
    val artists = viewModel.artists
    val categories = listOf(
        "Songs" to Icons.Default.MusicNote,
        "Albums" to Icons.Default.Album,
        "Artists" to Icons.Default.Person,
        "Genres" to Icons.Default.LibraryMusic,
        "Folders" to Icons.Default.FolderOpen
    )

    // Show album detail if album is selected
    if (selectedAlbum != null) {
        AlbumDetailScreenContent(
            albumName = selectedAlbum!!,
            songs = allSongs.filter { it.album.equals(selectedAlbum, ignoreCase = true) },
            musicViewModel = musicViewModel,
            savedViewModel = savedViewModel,
            savedSongs = savedSongs,
            onBack = { selectedAlbum = null }
        )
    } else if (selectedGenre != null) {
        // Show genre detail if genre is selected
        GenreDetailScreenContent(
            genreName = selectedGenre!!,
            songs = allSongs.filter { it.genre.equals(selectedGenre, ignoreCase = true) },
            musicViewModel = musicViewModel,
            savedViewModel = savedViewModel,
            savedSongs = savedSongs,
            onBack = { selectedGenre = null }
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
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (label, icon) ->
                        LibraryCategoryChip(
                            label = label,
                            icon = icon,
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
                    "Songs" -> LibrarySongsList(
                        songs = allSongs,
                        searchQuery = searchQuery,
                        musicViewModel = musicViewModel,
                        savedViewModel = savedViewModel,
                        savedSongs = savedSongs
                    )
                    "Albums" -> {
                        val albums = allSongs.groupBy { it.album }.map { (albumName, songs) ->
                            Triple(
                                albumName.ifEmpty { "Unknown Album" },
                                songs.firstOrNull()?.artist ?: "Unknown Artist",
                                songs.size
                            )
                        }
                        LibraryAlbumsList(
                            albums = albums,
                            allSongs = allSongs,
                            searchQuery = searchQuery,
                            onAlbumClick = { albumName ->
                                selectedAlbum = albumName
                            }
                        )
                    }
                    "Artists" -> {
                        val artistsList = allSongs.groupBy { it.artist }.map { (artistName, songs) ->
                            Triple(
                                artistName.ifEmpty { "Unknown Artist" },
                                songs.size,
                                songs.map { it.album }.distinct().size
                            )
                        }
                        LibraryArtistsList(
                            artists = artistsList,
                            searchQuery = searchQuery,
                            onArtistClick = { artistName ->
                                // Navigate to ArtistActivity
                                val intent = android.content.Intent(context, ArtistActivity::class.java)
                                intent.putExtra("ARTIST_NAME", artistName)
                                context.startActivity(intent)
                            }
                        )
                    }
                    "Genres" -> {
                        val genres = allSongs.groupBy { it.genre }.map { (genreName, songs) ->
                            Pair(genreName.ifEmpty { "Unknown Genre" }, songs.size)
                        }
                        LibraryGenresList(
                            genres = genres,
                            searchQuery = searchQuery,
                            onGenreClick = { genreName ->
                                selectedGenre = genreName
                            }
                        )
                    }
                    else -> SimpleCategoryScreen(selectedCategory)
                }
            }
        }
    }
}

@Composable
fun LibraryArtistList(
    artists: List<LibraryArtist>,
    searchQuery: String,
    onArtistClick: (LibraryArtist) -> Unit
) {
    val filteredArtists = artists.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(filteredArtists) { artist ->
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
            // Use Material Icon instead of painterResource
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = artist.name,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
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
fun LibraryCategoryChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                imageVector = icon,
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
fun AlbumDetailScreenContent(
    albumName: String,
    songs: List<Song>,
    musicViewModel: MusicViewModel,
    savedViewModel: SavedViewModel,
    savedSongs: List<Song>,
    onBack: () -> Unit
) {
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
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .clickable { onBack() }
                    .size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = albumName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${songs.size} Songs",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        // Songs List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(songs) { song ->
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

@Composable
fun LibraryAlbumSongItem(
    song: Song,
    musicViewModel: MusicViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            // Album art
            if (song.coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = song.title,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF6B4FA0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = song.title,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist,
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp
                )
            }

            Text(
                text = song.durationFormatted,
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp
            )

            IconButton(
                onClick = { musicViewModel.toggleFavorite(song) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) Color(0xFFEC4899) else Color.White,
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
fun GenreDetailScreenContent(
    genreName: String,
    songs: List<Song>,
    musicViewModel: MusicViewModel,
    savedViewModel: SavedViewModel,
    savedSongs: List<Song>,
    onBack: () -> Unit
) {
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
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .clickable { onBack() }
                    .size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = genreName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${songs.size} Songs",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        // Songs List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(songs) { song ->
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

@Composable
fun SimpleCategoryScreen(category: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = when(category) {
                    "Songs" -> Icons.Default.MusicNote
                    "Artists" -> Icons.Default.Person
                    "Genres" -> Icons.Default.LibraryMusic
                    "Folders" -> Icons.Default.FolderOpen
                    else -> Icons.Default.Album
                },
                contentDescription = category,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            Text(
                "$category Screen - Coming Soon",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LibrarySongsList(
    songs: List<Song>,
    searchQuery: String,
    musicViewModel: MusicViewModel,
    savedViewModel: SavedViewModel,
    savedSongs: List<Song>
) {
    val filteredSongs = songs.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.artist.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filteredSongs) { song ->
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

@Composable
fun LibrarySongCard(song: Song, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            // Album art
            Box(
                modifier = Modifier
                    .size(60.dp)
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = song.artist,
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp
                )
            }

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun LibraryAlbumsList(
    albums: List<Triple<String, String, Int>>,
    allSongs: List<Song>,
    searchQuery: String,
    onAlbumClick: (String) -> Unit
) {
    val filteredAlbums = albums.filter {
        it.first.contains(searchQuery, ignoreCase = true) ||
        it.second.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(filteredAlbums.size) { index ->
            val album = filteredAlbums[index]
            val coverUrl = allSongs.firstOrNull { it.album == album.first }?.coverUrl ?: ""
            LibraryAlbumCard(
                albumName = album.first,
                artistName = album.second,
                songCount = album.third,
                coverUrl = coverUrl,
                onClick = { onAlbumClick(album.first) }
            )
        }
    }
}

@Composable
fun LibraryAlbumCard(
    albumName: String,
    artistName: String,
    songCount: Int,
    coverUrl: String,
    onClick: () -> Unit
) {
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
            if (coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = albumName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = albumName,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = albumName,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "$artistName • $songCount Songs",
            color = Color(0xFFB0B0B0),
            fontSize = 13.sp
        )
    }
}

@Composable
fun LibraryArtistsList(
    artists: List<Triple<String, Int, Int>>,
    searchQuery: String,
    onArtistClick: (String) -> Unit
) {
    val filteredArtists = artists.filter {
        it.first.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(filteredArtists.size) { index ->
            val artist = filteredArtists[index]
            LibraryArtistCardNew(
                artistName = artist.first,
                songCount = artist.second,
                albumCount = artist.third,
                onClick = { onArtistClick(artist.first) }
            )
        }
    }
}

@Composable
fun LibraryArtistCardNew(
    artistName: String,
    songCount: Int,
    albumCount: Int,
    onClick: () -> Unit
) {
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
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = artistName,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = artistName,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "$songCount Songs, $albumCount Albums",
            color = Color(0xFFB0B0B0),
            fontSize = 13.sp
        )
    }
}

@Composable
fun LibraryGenresList(
    genres: List<Pair<String, Int>>,
    searchQuery: String,
    onGenreClick: (String) -> Unit
) {
    val filteredGenres = genres.filter {
        it.first.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filteredGenres.size) { index ->
            val genre = filteredGenres[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGenreClick(genre.first) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3A4A7A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LibraryMusic,
                        contentDescription = genre.first,
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = genre.first,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${genre.second} Songs",
                            color = Color(0xFFB0B0B0),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}