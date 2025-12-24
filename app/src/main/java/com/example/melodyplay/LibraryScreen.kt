package com.example.melodyplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.compose.*
import com.example.melodyplay.model.LibraryArtist
import com.example.melodyplay.repository.LibraryRepoImpl
import com.example.melodyplay.viewmodel.LibraryViewModel

class LibraryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MelodyPlayTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "library") {
                    composable("library") { MusicLibraryScreen(navController) }
                    composable("songs") { SimpleScreen("Songs Screen") }
                    composable("albums") { SimpleScreen("Albums Screen") }
                    composable("artists") { SimpleScreen("Artists Screen") }
                    composable("genres") { SimpleScreen("Genres Screen") }
                    composable("folders") { SimpleScreen("Folders Screen") }
                    composable("profile") { SimpleScreen("Profile Screen") }
                }
            }
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

// ------------------ Data ------------------



// ------------------ Music Library Screen ------------------
@Composable
fun MusicLibraryScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val viewModel = remember { LibraryViewModel(repository = LibraryRepoImpl()) }

    val selectedCategory by viewModel.selectedCategory
    val artists = viewModel.artists
    val categories = listOf(
        "Songs" to R.drawable.baseline_music_note_24,
        "Albums" to R.drawable.baseline_album_24,
        "Artists" to R.drawable.baseline_person_24,
        "Genres" to R.drawable.baseline_library_music_24,
        "Folders" to R.drawable.baseline_folder_open_24
    )

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
                    categories.forEach { (label, iconRes) ->
                        CategoryChip(
                            label = label,
                            iconResId = iconRes,
                            selected = selectedCategory == label
                        ) {
                            viewModel.selectCategory(label)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF6176E3)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ArtistListScreen(artists = artists) { /* handle artist click */ }
        }
    }
}

    // ------------------ Artist List ------------------
@Composable
fun ArtistListScreen(artists: List<LibraryArtist>, onArtistClick: (LibraryArtist) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3C72))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(artists) { artist ->
            ArtistCard(artist = artist) {
                onArtistClick(artist)
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
        MusicLibraryScreen(navController)
    }
}
