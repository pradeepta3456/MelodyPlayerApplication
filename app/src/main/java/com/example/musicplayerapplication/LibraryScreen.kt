package com.example.musicplayerapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.LibraryRepoImpl
import com.example.musicplayerapplication.viewmodel.LibraryViewModel

class LibraryScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MelodyPlayTheme {
                AppNavGraph()
            }
        }
    }
}


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "library"
    ) {
        composable("library") { LibraryScreen(navController) }
        composable("songs") { SimpleScreen("Songs Screen") }
        composable("albums") { SimpleScreen("Albums Screen") }
        composable("artists") { SimpleScreen("Artists Screen") }
        composable("genres") { SimpleScreen("Genres Screen") }
        composable("folders") { SimpleScreen("Folders Screen") }
        composable("profile") { SimpleScreen("Profile Screen") }
        composable(
            route = "album_detail/{albumName}",
            arguments = listOf(navArgument("albumName") { type = NavType.StringType })
        ) { backStackEntry ->
            val albumName = backStackEntry.arguments?.getString("albumName") ?: ""
            AlbumDetailScreen(albumName = albumName, navController = navController)
        }
    }
}



// ------------------ Theme ------------------
@Composable
fun MelodyPlayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF7B8BC4),
            secondary = Color(0xFF4F649F),
            background = Color(0xFF2B3A6B)
        ),
        content = content
    )
}





// ------------------ Music Library Screen ------------------
@Composable
fun LibraryScreen(navController: NavController) {

    val viewModel = remember { LibraryViewModel(repository = LibraryRepoImpl()) }

    var searchQuery by remember { mutableStateOf("") }
    val artists = viewModel.artists
    val categories = listOf(
        "Songs" to (R.drawable.baseline_music_note_24 to "songs"),
        "Albums" to (R.drawable.baseline_album_24 to "albums"),
        "Artists" to (R.drawable.baseline_person_24 to "artists"),
        "Genres" to (R.drawable.baseline_library_music_24 to "genres"),
        "Folders" to (R.drawable.baseline_folder_open_24 to "folders")
    )
    
    // Fixed category for this screen is "Albums"
    val fixedCategory = "Albums"

    Scaffold(
        topBar = {
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
                    categories.forEach { (label, iconAndRoute) ->
                        val (iconRes, route) = iconAndRoute
                        CategoryChip(
                            label = label,
                            iconResId = iconRes,
                            selected = label == fixedCategory
                        ) {
                            // If not Albums, navigate to other screen
                            if (label != fixedCategory) {
                                navController.navigate(route)
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF6176E3)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ArtistListScreen(artists = artists, navController = navController)
        }
    }
}

// ------------------ Artist List ------------------
@Composable
fun ArtistListScreen(artists: List<LibraryArtist>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3C72))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(artists) { artist ->
            ArtistCard(artist = artist) {
                navController.navigate("album_detail/${artist.name}")
            }
        }
    }
}

@Composable
fun ArtistCard(artist: LibraryArtist, onClick: () -> Unit) {
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

// ------------------ Chips ------------------
@Composable
fun CategoryChip(label: String, iconResId: Int, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = { Icon(painter = painterResource(id = iconResId), contentDescription = label, modifier = Modifier.size(18.dp)) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF9199B4),
            selectedLabelColor = Color.White,
            containerColor = Color(0xFF596791),
            labelColor = Color.White
        )
    )
}

// ------------------ Bottom Bar ------------------


// ------------------ Album Detail Screen ------------------
@Composable
fun AlbumDetailScreen(albumName: String, navController: NavController) {
    // Get different songs based on the artist name
    val songs = remember(albumName) {
        getSongsForArtist(albumName)
    }
    
    val durations = remember(albumName) {
        getDurationsForArtist(albumName)
    }
    
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF414C91))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .size(24.dp)
                )
                Text(
                    text = albumName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color(0xFF2C3C72)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF2C3C72))
        ) {
            // Songs List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(songs.size) { index ->
                    AlbumSongItem(
                        song = songs[index],
                        duration = durations[index],
                        onSongClick = { /* Navigate to song detail or play */ }
                    )
                }
                
                // Sign in promotional section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SignInPromoCard()
                }
            }
        }
    }
}

@Composable
fun AlbumSongItem(
    song: Song,
    duration: String,
    onSongClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSongClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A4A7A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Album Art Thumbnail
            Image(
                painter = painterResource(id = song.cover),
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            // Song Name and Duration in same line
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
            
            // Heart Icon
            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFEC4899) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Play/Pause Icon
            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // More Options Icon
            IconButton(
                onClick = { /* Show menu */ },
                modifier = Modifier.size(40.dp)
            ) {
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
fun SignInPromoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A3A6A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sign in Button
            Button(
                onClick = { /* Navigate to sign in */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
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
            
            // Promotional Text
            Text(
                text = "Create more Playlist and Costumize your music",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

// ------------------ Helper Functions for Artist Details ------------------
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
            Song(2, "Bright ", "Artist", R.drawable.img_11, 0),
            Song(3, "Golden", "Artist", R.drawable.img_12, 0),
            Song(4, "Summe", "Artist", R.drawable.img14, 0),
            Song(5, "Radian", "Artist", R.drawable.img15, 0)
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
            Song(3, "Nigh", "Artist", R.drawable.img_6, 0),
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

// ------------------ Simple Placeholder Screen ------------------
@Composable
fun SimpleScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF2B3A6B)), contentAlignment = Alignment.Center) {
        Text(title, color = Color.White, fontSize = 24.sp)
    }
}


// ------------------ Preview ------------------
@Preview(showBackground = true)
@Composable
fun MelodyPlayPreview() {
    MelodyPlayTheme {
        val navController = rememberNavController()
        LibraryScreen(navController)
    }
}
