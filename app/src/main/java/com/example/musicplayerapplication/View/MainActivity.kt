package com.example.musicplayerapplication.View
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalMusicUIScreen()
        }
    }
}

// Updated SongData to accept optional image URL instead of drawable resource
data class SongData(
    val title: String,
    val artist: String,
    val imageUrl: String? = null, // Changed from Int to String? for URL
    val duration: String
)

@Composable
fun FinalMusicUIScreen() {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Genres") }

    // Updated list without hardcoded drawable resources
    val list = listOf(
        SongData("Katseye", "Black eye", null, "2:50"),
        SongData("No More", "LILLY", null, "3:05"),
        SongData("Ocean Eyes", "Billie eilish", null, "3:54"),
        SongData("Sunrise", "Heat Waves", null, "2:45"),
        SongData("Bite Me", "Risern", null, "5:12")
    )

    // Filter songs based on search query
    val filteredList = if (searchQuery.isBlank()) {
        list
    } else {
        list.filter { song ->
            song.title.contains(searchQuery, ignoreCase = true) ||
                    song.artist.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF061A7A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
            Spacer(Modifier.height(18.dp))
            TabsRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            Spacer(Modifier.height(18.dp))

            // Show message if no results found
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No songs found",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            } else {
                SongsList(filteredList)
            }

            Spacer(Modifier.height(24.dp))
            GenreSection()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Artist, song and names", color = Color.White.copy(alpha = 0.7f)) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0x33FFFFFF),
            unfocusedContainerColor = Color(0x33FFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun TabsRow(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        TabChipWithIcon(
            text = "Songs",
            icon = Icons.Default.MusicNote,
            isSelected = selectedTab == "Songs",
            onClick = { onTabSelected("Songs") }
        )
        TabChipWithIcon(
            text = "Artists",
            icon = Icons.Default.Person,
            isSelected = selectedTab == "Artists",
            onClick = { onTabSelected("Artists") }
        )
        TabChipWithIcon(
            text = "Albums",
            icon = Icons.Default.Album,
            isSelected = selectedTab == "Albums",
            onClick = { onTabSelected("Albums") }
        )
        TabChipWithIcon(
            text = "Genres",
            icon = Icons.Default.LibraryMusic,
            isSelected = selectedTab == "Genres",
            onClick = { onTabSelected("Genres") }
        )
        TabChipWithIcon(
            text = "Folders",
            icon = Icons.Default.Folder,
            isSelected = selectedTab == "Folders",
            onClick = { onTabSelected("Folders") }
        )
    }
}

@Composable
fun TabChipWithIcon(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF9D77FF) else Color(0x33FFFFFF))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun SongsList(list: List<SongData>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        list.forEach { song ->
            SongCard(
                song = song,
                onFavoriteClick = {
                    println("Favorite clicked: ${song.title}")
                },
                onMoreClick = {
                    println("More clicked: ${song.title}")
                },
                onSongClick = {
                    println("Song clicked: ${song.title}")
                }
            )
        }
    }
}

@Composable
fun SongCard(
    song: SongData,
    onFavoriteClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onSongClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x773E4A9F), RoundedCornerShape(22.dp))
            .clickable { onSongClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art or music note icon
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF9D77FF).copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (song.imageUrl != null) {
                // TODO: Use Coil or Glide to load image from URL
                // Example with Coil:
                // AsyncImage(
                //     model = song.imageUrl,
                //     contentDescription = null,
                //     modifier = Modifier.fillMaxSize(),
                //     contentScale = ContentScale.Crop
                // )

                // Placeholder until image loading library is added
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(30.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Text(song.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(song.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }

        Text(song.duration, color = Color.White, fontSize = 14.sp)
        Spacer(Modifier.width(12.dp))

        IconButton(onClick = { onFavoriteClick() }) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = Color.White
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = { onMoreClick() }) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun GenreSection() {
    val items = listOf(
        "Pop" to Color(0xFFFFE066),
        "Rock" to Color(0xFF8EFF8B),
        "Hiphop" to Color(0xFFFF5A47),
        "Jazz" to Color(0xFF4A2A24),
        "R & B" to Color(0xFF9EE7F6),
        "K-pop" to Color(0xFF9B7BFF)
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        for (i in items.indices step 2) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GenreBox(items[i].first, items[i].second, Modifier.weight(1f))
                if (i + 1 < items.size) {
                    GenreBox(items[i + 1].first, items[i + 1].second, Modifier.weight(1f))
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun GenreBox(name: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(color)
    ) {
        // Decorative tilted rectangle
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 20.dp)
                .size(80.dp)
                .rotate(15f)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.5f))
        )

        // Text
        Text(
            name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
        )
    }
}