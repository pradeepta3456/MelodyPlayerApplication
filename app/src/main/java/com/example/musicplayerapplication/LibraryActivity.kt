package com.example.musicplayerapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayerapplication.model.Song

class LibraryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicLibraryTheme {
                MusicLibraryApp()
            }
        }
    }
}

@Composable
fun MusicLibraryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF9333EA),
            background = Color(0xFF581C87)
        ),
        content = content
    )
}


@Composable
fun MusicLibraryApp() {
    var selectedNavItem by remember { mutableStateOf("Library") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF581C87),
                        Color(0xFF6B21A8),
                        Color(0xFF581C87)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedNavItem) {
                    "Home" -> CenteredText("Home Screen")
                    "Search" -> CenteredText("Search Screen")
                    "Library" -> MusicLibraryScreen()
                    "Settings" -> CenteredText("Settings Screen")
                }
            }

            // Bottom Navigation
            CustomBottomNav(
                selected = selectedNavItem,
                onSelect = { selectedNavItem = it }
            )
        }
    }
}

@Composable
fun CenteredText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun CustomBottomNav(
    selected: String,
    onSelect: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color(0xFF6B21A8),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItemWithVector(
                label = "Home",
                icon = Icons.Default.Home,
                isSelected = selected == "Home",
                onClick = { onSelect("Home") }
            )
            NavItemWithVector(
                label = "Search",
                icon = Icons.Default.Search,
                isSelected = selected == "Search",
                onClick = { onSelect("Search") }
            )
            NavItemWithDrawable(
                label = "Library",
                iconRes = R.drawable.baseline_library_music_24,
                isSelected = selected == "Library",
                onClick = { onSelect("Library") }
            )
            NavItemWithVector(
                label = "Settings",
                icon = Icons.Default.Settings,
                isSelected = selected == "Settings",
                onClick = { onSelect("Settings") }
            )
        }
    }
}

@Composable
fun NavItemWithVector(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = if (isSelected) Color.White else Color(0xFFD8B4FE)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color(0xFFD8B4FE),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun NavItemWithDrawable(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = if (isSelected) Color.White else Color(0xFFD8B4FE)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color(0xFFD8B4FE),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun MusicLibraryScreen() {
    var selectedTab by remember { mutableStateOf("Songs") }
    val tabs = listOf("Albums", "Artists", "Songs")

    val songs = remember {
        listOf(
            Song(1, "Song Name", "Artist Name", R.drawable.img_10),
            Song(2, "Song Name", "Artist Name", R.drawable.img_11),
            Song(3, "Song Name", "Artist Name", R.drawable.img14),
            Song(4, "Song Name", "Artist Name", R.drawable.img15),
            Song(5, "Song Name", "Artist Name", R.drawable.img_12)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Text(
            text = "Your Library",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "All your music in one place",
            fontSize = 14.sp,
            color = Color(0xFFD8B4FE),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            color = Color(0x807C3AED)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEach { tab ->
                    TabButton(
                        text = tab,
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Song List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(songs) { song ->
                SongItem(song = song)
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selected) Color(0xFF9333EA) else Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Color(0xFFE9D5FF),
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun SongItem(song: Song) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle song click */ },
        shape = RoundedCornerShape(16.dp),
        color = Color(0x4D7C3AED)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            Image(
                painter = painterResource(id = song.cover),
                contentDescription = song.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Song Info
            Column {
                Text(
                    text = song.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist,
                    fontSize = 14.sp,
                    color = Color(0xFFD8B4FE)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MusicLibraryAppPreview() {
    MusicLibraryTheme {
        MusicLibraryApp()
    }
}