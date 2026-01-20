package com.example.musicplayerapplication.View
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayerapplication.R


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalMusicUIScreen()
        }
    }
}

data class SongData(
    val title: String,
    val artist: String,
    val image: Int,
    val duration: String
)

@Composable
fun FinalMusicUIScreen() {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Genres") }

    val list = listOf(
        SongData("Katseye", "Black eye", R.drawable.img_10, "2:50"),
        SongData("No More", "LILLY", R.drawable.img_11, "3:05"),
        SongData("Ocean Eyes", "Billie eilish", R.drawable.img_12, "3:54"),
        SongData("Sunrise", "Heat Waves", R.drawable.img_11, "2:45"),
        SongData("Bite Me", "Risern", R.drawable.img_12, "5:12")
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
                SongsList(filteredList,)
            }

            Spacer(Modifier.height(24.dp))
            GenreSection()
            Spacer(Modifier.height(16.dp)) // Bottom padding for comfortable scrolling
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
                painter = painterResource(R.drawable.baseline_menu_24),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                        contentDescription = "Clear search",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                Icon(
                    painter = painterResource(R.drawable.baseline_search_24),
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
            .horizontalScroll(rememberScrollState()) // Make tabs scrollable horizontally if needed
    ) {
        TabChipWithIcon(
            text = "Songs",
            iconRes = R.drawable.baseline_music_note_24,
            isSelected = selectedTab == "Songs",
            onClick = { onTabSelected("Songs") }
        )
        TabChipWithIcon(
            text = "Artists",
            iconRes = R.drawable.baseline_person_24,
            isSelected = selectedTab == "Artists",
            onClick = { onTabSelected("Artists") }
        )
        TabChipWithIcon(
            text = "Albums",
            iconRes = R.drawable.baseline_album_24,
            isSelected = selectedTab == "Albums",
            onClick = { onTabSelected("Albums") }
        )
        TabChipWithIcon(
            text = "Genres",
            iconRes = R.drawable.baseline_library_music_24,
            isSelected = selectedTab == "Genres",
            onClick = { onTabSelected("Genres") }
        )
        TabChipWithIcon(
            text = "Folders",
            iconRes = R.drawable.baseline_folder_24,
            isSelected = selectedTab == "Folders",
            onClick = { onTabSelected("Folders") }
        )
    }
}

@Composable
fun TabChipWithIcon(
    text: String,
    iconRes: Int,
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
            painter = painterResource(iconRes),
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
                song= song,
                onFavoriteClick = {
                    val it = null
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
    onSongClick: () -> Unit ={}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x773E4A9F), RoundedCornerShape(22.dp))
            .clickable {onSongClick(song) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(song.image),
            contentDescription = null,
            modifier = Modifier
                .size(55.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Text(song.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(song.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }

        Text(song.duration, color = Color.White, fontSize = 14.sp)
        Spacer(Modifier.width(12.dp))

        IconButton(onClick = { onFavoriteClick() }) {
            Icon(
                painter = painterResource(R.drawable.baseline_favorite_24),
                contentDescription = "Favorite",
                tint = Color.White
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = { onMoreClick() }) {
        }
        Icon(
            painter = painterResource(R.drawable.baseline_more_horiz_24),
            contentDescription = null,
            tint = Color.White
        )
    }
}

fun onSongClick(song: SongData) {}

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