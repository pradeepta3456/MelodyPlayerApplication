//package com.example.musicplayerapplication.View
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.foundation.horizontalScroll
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.musicplayerapplication.R
//
//class FolderActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            MaterialTheme {
//                FolderAppScreen()
//            }
//        }
//    }
//}
//
//data class FolderItem(
//    val name: String,
//    val songCount: Int,
//    val fileCount: Int,
//    val color1: Color,
//    val color2: Color
//)
//
//data class SongItem(
//    val title: String,
//    val artist: String,
//    val duration: String
//)
//
//@Composable
//fun FolderAppScreen() {
//    var currentFolder by remember { mutableStateOf<FolderItem?>(null) }
//    var currentScreen by remember { mutableStateOf("Home") }
//
//    when {
//        currentFolder != null -> {
//            FolderContentScreen(
//                folder = currentFolder!!,
//                onBackClick = {
//                    currentFolder = null
//                },
//                selectedNavItem = currentScreen,
//                onNavItemClick = { item ->
//                    currentFolder = null
//                    currentScreen = item
//                }
//            )
//        }
//        currentScreen == "Home" -> {
//            HomeScreen(
//                selectedNavItem = currentScreen,
//                onNavItemClick = { item ->
//                    currentScreen = item
//                }
//            )
//        }
//        currentScreen == "Library" -> {
//            FolderLibraryScreen(
//                onFolderClick = { folder ->
//                    currentFolder = folder
//                },
//                selectedNavItem = currentScreen,
//                onNavItemClick = { item ->
//                    currentScreen = item
//                }
//            )
//        }
//        currentScreen == "Search" -> {
//            SearchScreen(
//                selectedNavItem = currentScreen,
//                onNavItemClick = { item ->
//                    currentScreen = item
//                }
//            )
//        }
//        currentScreen == "Playlists" -> {
//            PlaylistsScreen(
//                selectedNavItem = currentScreen,
//                onNavItemClick = { item ->
//                    currentScreen = item
//                }
//            )
//        }
//        currentScreen == "Settings" -> {
//            SettingsScreen(
//                selectedNavItem = currentScreen,
//                onNavItemClick = { item ->
//                    currentScreen = item
//                }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FolderLibraryScreen(
//    onFolderClick: (FolderItem) -> Unit,
//    selectedNavItem: String,
//    onNavItemClick: (String) -> Unit
//) {
//    var searchQuery by remember { mutableStateOf("") }
//    var selectedTab by remember { mutableStateOf("Folders") }
//    var isPlaying by remember { mutableStateOf(true) }
//    val context = LocalContext.current
//
//    val folders = listOf(
//        FolderItem("Downloads", 32, 4, Color(0xFF4C5DF0), Color(0xFF6F7CFC)),
//        FolderItem("Recordings", 24, 3, Color(0xFF52C1F0), Color(0xFF7DDAFF)),
//        FolderItem("WhatsApp Audio", 32, 4, Color(0xFF9C43F0), Color(0xFFC06CFF)),
//        FolderItem("Music", 45, 5, Color(0xFF43E08B), Color(0xFF70FFB3)),
//        FolderItem("Podcasts", 28, 2, Color(0xFFF06292), Color(0xFFF48FB1)),
//        FolderItem("Audiobooks", 15, 3, Color(0xFFFF9800), Color(0xFFFFB74D))
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(Color(0xFF3D4B8E), Color(0xFF1A2456))
//                )
//            )
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Top Section with Search Bar
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                // Search Bar
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .clip(RoundedCornerShape(28.dp))
//                        .background(Color(0xFF5B6AA8))
//                        .padding(horizontal = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.baseline_menu_24),
//                        contentDescription = "Menu",
//                        tint = Color.White,
//                        modifier = Modifier
//                            .size(24.dp)
//                            .clickable {
//                                Toast
//                                    .makeText(context, "Menu opened", Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                    )
//
//                    Spacer(modifier = Modifier.width(12.dp))
//
//                    BasicTextField(
//                        value = searchQuery,
//                        onValueChange = { searchQuery = it },
//                        modifier = Modifier.weight(1f),
//                        textStyle = LocalTextStyle.current.copy(
//                            color = Color.White,
//                            fontSize = 16.sp
//                        ),
//                        decorationBox = { innerTextField ->
//                            if (searchQuery.isEmpty()) {
//                                Text(
//                                    text = "Folder names",
//                                    color = Color.White.copy(alpha = 0.5f),
//                                    fontSize = 16.sp
//                                )
//                            }
//                            innerTextField()
//                        },
//                        singleLine = true
//                    )
//
//                    Spacer(modifier = Modifier.width(12.dp))
//
//                    Icon(
//                        painter = painterResource(id = R.drawable.baseline_search_24),
//                        contentDescription = "Search",
//                        tint = Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Tab Row
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .horizontalScroll(rememberScrollState()),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    TabButton(
//                        text = "Songs",
//                        iconRes = R.drawable.baseline_music_note_24,
//                        selected = selectedTab == "Songs",
//                        onClick = {
//                            selectedTab = "Songs"
//                            Toast.makeText(context, "Songs tab", Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                    TabButton(
//                        text = "Artists",
//                        iconRes = R.drawable.baseline_person_24,
//                        selected = selectedTab == "Artists",
//                        onClick = {
//                            selectedTab = "Artists"
//                            Toast.makeText(context, "Artists tab", Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                    TabButton(
//                        text = "Albums",
//                        iconRes = R.drawable.baseline_album_24,
//                        selected = selectedTab == "Albums",
//                        onClick = {
//                            selectedTab = "Albums"
//                            Toast.makeText(context, "Albums tab", Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                    TabButton(
//                        text = "Genres",
//                        iconRes = R.drawable.baseline_category_24,
//                        selected = selectedTab == "Genres",
//                        onClick = {
//                            selectedTab = "Genres"
//                            Toast.makeText(context, "Genres tab", Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                    TabButton(
//                        text = "Folders",
//                        iconRes = R.drawable.baseline_folder_24,
//                        selected = selectedTab == "Folders",
//                        onClick = {
//                            selectedTab = "Folders"
//                            Toast.makeText(context, "Folders tab", Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                }
//            }
//
//            // Grid of Folders
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//                modifier = Modifier.weight(1f)
//            ) {
//                val filteredFolders = folders.filter {
//                    it.name.contains(searchQuery, ignoreCase = true)
//                }
//
//                items(filteredFolders) { folder ->
//                    FolderGridCard(
//                        folder = folder,
//                        onClick = {
//                            onFolderClick(folder)
//                        }
//                    )
//                }
//            }
//
//            // Bottom Player Bar
//            MiniPlayerBar(
//                isPlaying = isPlaying,
//                onPlayPauseClick = {
//                    isPlaying = !isPlaying
//                },
//                onLikeClick = { },
//                onPreviousClick = { },
//                onNextClick = { }
//            )
//
//            // Bottom Navigation Bar
//            BottomNavigationBar(
//                selectedItem = selectedNavItem,
//                onItemClick = onNavItemClick
//            )
//        }
//    }
//}
//
//@Composable
//fun FolderContentScreen(
//    folder: FolderItem,
//    onBackClick: () -> Unit,
//    selectedNavItem: String,
//    onNavItemClick: (String) -> Unit
//) {
//    val context = LocalContext.current
//    var isPlaying by remember { mutableStateOf(true) }
//
//    // Sample songs for the folder
//    val songs = remember {
//        List(folder.songCount) { index ->
//            SongItem(
//                title = "Song ${index + 1}",
//                artist = "Artist ${(index % 5) + 1}",
//                duration = "${(index % 4) + 2}:${(index % 60).toString().padStart(2, '0')}"
//            )
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(Color(0xFF3D4B8E), Color(0xFF1A2456))
//                )
//            )
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Header with back button
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color(0xFF4A5A9F))
//                    .padding(16.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.outline_arrow_back_24),
//                        contentDescription = "Back",
//                        colorFilter = ColorFilter.tint(Color.White),
//                        modifier = Modifier
//                            .size(28.dp)
//                            .clickable(
//                                onClick = onBackClick,
//                                indication = null,
//                                interactionSource = remember { MutableInteractionSource() }
//                            )
//                    )
//
//                    Spacer(modifier = Modifier.width(16.dp))
//
//                    Column {
//                        Text(
//                            text = folder.name,
//                            color = Color.White,
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            text = "${folder.songCount} Songs",
//                            color = Color.White.copy(alpha = 0.7f),
//                            fontSize = 14.sp
//                        )
//                    }
//                }
//            }
//
//            // Songs List
//            LazyColumn(
//                modifier = Modifier.weight(1f),
//                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
//            ) {
//                items(songs) { song ->
//                    SongListItem(
//                        song = song,
//                        onClick = {
//                            Toast.makeText(context, "Playing ${song.title}", Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                }
//            }
//
//            // Bottom Player Bar
//            MiniPlayerBar(
//                isPlaying = isPlaying,
//                onPlayPauseClick = {
//                    isPlaying = !isPlaying
//                },
//                onLikeClick = { },
//                onPreviousClick = { },
//                onNextClick = { }
//            )
//
//            // Bottom Navigation Bar
//            BottomNavigationBar(
//                selectedItem = selectedNavItem,
//                onItemClick = onNavItemClick
//            )
//        }
//    }
//}
//
//@Composable
//fun SongListItem(
//    song: SongItem,
//    onClick: () -> Unit
//) {
//    val context = LocalContext.current
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(
//                onClick = onClick,
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() }
//            )
//            .padding(vertical = 12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // Song icon/thumbnail
//        Box(
//            modifier = Modifier
//                .size(48.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .background(Color(0xFF5B6AA8)),
//            contentAlignment = Alignment.Center
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.baseline_music_note_24),
//                contentDescription = "Music",
//                colorFilter = ColorFilter.tint(Color.White),
//                modifier = Modifier.size(24.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        // Song details
//        Column(modifier = Modifier.weight(1f)) {
//            Text(
//                text = song.title,
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium
//            )
//            Text(
//                text = song.artist,
//                color = Color.White.copy(alpha = 0.6f),
//                fontSize = 14.sp
//            )
//        }
//
//        // Duration
//        Text(
//            text = song.duration,
//            color = Color.White.copy(alpha = 0.6f),
//            fontSize = 14.sp
//        )
//
//        Spacer(modifier = Modifier.width(8.dp))
//
//        // More options
//        Image(
//            painter = painterResource(id = R.drawable.baseline_more_vert_24),
//            contentDescription = "More",
//            colorFilter = ColorFilter.tint(Color.White),
//            modifier = Modifier
//                .size(24.dp)
//                .clickable(
//                    onClick = {
//                        Toast
//                            .makeText(context, "More options for ${song.title}", Toast.LENGTH_SHORT)
//                            .show()
//                    },
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                )
//        )
//    }
//}
//
//@Composable
//fun TabButton(
//    text: String,
//    iconRes: Int,
//    selected: Boolean,
//    onClick: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .height(40.dp)
//            .clip(RoundedCornerShape(20.dp))
//            .background(if (selected) Color.White else Color.White.copy(alpha = 0.2f))
//            .clickable(
//                onClick = onClick,
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() }
//            )
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(6.dp)
//        ) {
//            Icon(
//                painter = painterResource(id = iconRes),
//                contentDescription = text,
//                tint = if (selected) Color(0xFF3D4B8E) else Color.White,
//                modifier = Modifier.size(18.dp)
//            )
//            Text(
//                text = text,
//                color = if (selected) Color(0xFF3D4B8E) else Color.White,
//                fontSize = 14.sp,
//                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
//            )
//        }
//    }
//}
//
//@Composable
//fun FolderGridCard(
//    folder: FolderItem,
//    onClick: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(
//                onClick = onClick,
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() }
//            )
//    ) {
//        // Folder Image/Icon
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(1f)
//                .clip(RoundedCornerShape(16.dp))
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(folder.color1, folder.color2)
//                    )
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.baseline_folder_24),
//                contentDescription = folder.name,
//                tint = Color.White,
//                modifier = Modifier.size(64.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Folder Name
//        Text(
//            text = folder.name,
//            color = Color.White,
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Medium,
//            maxLines = 1
//        )
//
//        // File Count
//        Text(
//            text = "${folder.songCount} Songs, ${folder.fileCount} Albums",
//            color = Color.White.copy(alpha = 0.6f),
//            fontSize = 13.sp,
//            maxLines = 1
//        )
//    }
//}
//
//@Composable
//fun MiniPlayerBar(
//    isPlaying: Boolean,
//    onPlayPauseClick: () -> Unit,
//    onLikeClick: () -> Unit,
//    onPreviousClick: () -> Unit,
//    onNextClick: () -> Unit
//) {
//    val context = LocalContext.current
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(72.dp)
//            .background(Color(0xFF4A5A9F))
//            .clickable { }
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxSize(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Album Art
//            Box(
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(Color(0xFF5B6AA8))
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            // Song Title
//            Text(
//                text = "Ocean Waves",
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium,
//                modifier = Modifier.weight(1f)
//            )
//
//            // Controls
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.baseline_favorite_24),
//                    contentDescription = "Like",
//                    colorFilter = ColorFilter.tint(Color.White),
//                    modifier = Modifier
//                        .size(24.dp)
//                        .clickable(
//                            onClick = onLikeClick,
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() }
//                        )
//                )
//                Image(
//                    painter = painterResource(id = R.drawable.baseline_skip_previous_24),
//                    contentDescription = "Previous",
//                    colorFilter = ColorFilter.tint(Color.White),
//                    modifier = Modifier
//                        .size(28.dp)
//                        .clickable(
//                            onClick = onPreviousClick,
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() }
//                        )
//                )
//                Image(
//                    painter = painterResource(
//                        id = if (isPlaying) R.drawable.baseline_pause_24
//                        else R.drawable.baseline_play_arrow_24
//                    ),
//                    contentDescription = if (isPlaying) "Pause" else "Play",
//                    colorFilter = ColorFilter.tint(Color.White),
//                    modifier = Modifier
//                        .size(32.dp)
//                        .clickable(
//                            onClick = onPlayPauseClick,
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() }
//                        )
//                )
//                Image(
//                    painter = painterResource(id = R.drawable.baseline_skip_next_24),
//                    contentDescription = "Next",
//                    colorFilter = ColorFilter.tint(Color.White),
//                    modifier = Modifier
//                        .size(28.dp)
//                        .clickable(
//                            onClick = onNextClick,
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() }
//                        )
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomNavigationBar(
//    selectedItem: String,
//    onItemClick: (String) -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(72.dp)
//            .background(Color(0xFF2A3664))
//            .padding(horizontal = 8.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        BottomNavItem(
//            iconRes = R.drawable.baseline_home_24,
//            label = "Home",
//            selected = selectedItem == "Home",
//            onClick = {
//                onItemClick("Home")
//            }
//        )
//        BottomNavItem(
//            iconRes = R.drawable.baseline_library_music_24,
//            label = "Library",
//            selected = selectedItem == "Library",
//            onClick = {
//                onItemClick("Library")
//            }
//        )
//        BottomNavItem(
//            iconRes = R.drawable.baseline_search_24,
//            label = "Search",
//            selected = selectedItem == "Search",
//            onClick = {
//                onItemClick("Search")
//            }
//        )
//        BottomNavItem(
//            iconRes = R.drawable.baseline_playlist_play_24,
//            label = "Playlists",
//            selected = selectedItem == "Playlists",
//            onClick = {
//                onItemClick("Playlists")
//            }
//        )
//        BottomNavItem(
//            iconRes = R.drawable.baseline_settings_24,
//            label = "Settings",
//            selected = selectedItem == "Settings",
//            onClick = {
//                onItemClick("Settings")
//            }
//        )
//    }
//}
//
//@Composable
//fun BottomNavItem(
//    iconRes: Int,
//    label: String,
//    selected: Boolean,
//    onClick: () -> Unit
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier
//            .clickable(
//                onClick = onClick,
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() }
//            )
//            .padding(8.dp)
//    ) {
//        Image(
//            painter = painterResource(id = iconRes),
//            contentDescription = label,
//            colorFilter = ColorFilter.tint(Color.White),
//            modifier = Modifier.size(24.dp)
//        )
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(
//            text = label,
//            color = if (selected) Color.White else Color.White.copy(alpha = 0.5f),
//            fontSize = 11.sp,
//            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
//        )
//    }
//}
//
//@Composable
//fun HomeScreen(
//    selectedNavItem: String,
//    onNavItemClick: (String) -> Unit
//) {
//    var isPlaying by remember { mutableStateOf(true) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(Color(0xFF3D4B8E), Color(0xFF1A2456))
//                )
//            )
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Header
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color(0xFF4A5A9F))
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "Home",
//                    color = Color.White,
//                    fontSize = 32.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            // Content
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.baseline_home_24),
//                    contentDescription = "Home",
//                    tint = Color.White,
//                    modifier = Modifier.size(80.dp)
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = "Welcome Home",
//                    color = Color.White,
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "Discover your favorite music",
//                    color = Color.White.copy(alpha = 0.7f),
//                    fontSize = 16.sp
//                )
//            }
//
//            // Bottom Player Bar
//            MiniPlayerBar(
//                isPlaying = isPlaying,
//                onPlayPauseClick = {
//                    isPlaying = !isPlaying
//                },
//                onLikeClick = { },
//                onPreviousClick = { },
//                onNextClick = { }
//            )
//
//            // Bottom Navigation Bar
//            BottomNavigationBar(
//                selectedItem = selectedNavItem,
//                onItemClick = onNavItemClick
//            )
//        }
//    }
//}
//
//@Composable
//fun SearchScreen(
//    selectedNavItem: String,
//    onNavItemClick: (String) -> Unit
//) {
//    var searchQuery by remember { mutableStateOf("") }
//    var isPlaying by remember { mutableStateOf(true) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(Color(0xFF3D4B8E), Color(0xFF1A2456))
//                )
//            )
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Header
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color(0xFF4A5A9F))
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "Search",
//                    color = Color.White,
//                    fontSize = 32.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            // Search Bar
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .clip(RoundedCornerShape(28.dp))
//                        .background(Color(0xFF5B6AA8))
//                        .padding(horizontal = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.baseline_search_24),
//                        contentDescription = "Search",
//                        tint = Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
//
//                    Spacer(modifier = Modifier.width(12.dp))
//
//                    BasicTextField(
//                        value = searchQuery,
//                        onValueChange = { searchQuery = it },
//                        modifier = Modifier.weight(1f),
//                        textStyle = LocalTextStyle.current.copy(
//                            color = Color.White,
//                            fontSize = 16.sp
//                        ),
//                        decorationBox = { innerTextField ->
//                            if (searchQuery.isEmpty()) {
//                                Text(
//                                    text = "Search for songs, artists, albums...",
//                                    color = Color.White.copy(alpha = 0.5f),
//                                    fontSize = 16.sp
//                                )
//                            }
//                            innerTextField()
//                        },
//                        singleLine = true
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // Search suggestions or results
//                Text(
//                    text = "Recent Searches",
//                    color = Color.White,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            // Bottom Player Bar
//            MiniPlayerBar(
//                isPlaying = isPlaying,
//                onPlayPauseClick = {
//                    isPlaying = !isPlaying
//                },
//                onLikeClick = { },
//                onPreviousClick = { },
//                onNextClick = { }
//            )
//
//            // Bottom Navigation Bar
//            BottomNavigationBar(
//                selectedItem = selectedNavItem,
//                onItemClick = onNavItemClick
//            )
//        }
//    }
//}
//
//@Composable
//fun PlaylistsScreen(
//    selectedNavItem: String,
//    onNavItemClick: (String) -> Unit
//) {
//    val context = LocalContext.current
//    var isPlaying by remember { mutableStateOf(true) }
//
//    val playlists = listOf(
//        "My Favorites" to 45,
//        "Workout Mix" to 32,
//        "Chill Vibes" to 28,
//        "Road Trip" to 50,
//        "Sleep Sounds" to 20,
//        "Party Time" to 38
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(Color(0xFF3D4B8E), Color(0xFF1A2456))
//                )
//            )
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Header
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color(0xFF4A5A9F))
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "Playlists",
//                    color = Color.White,
//                    fontSize = 32.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            // Playlists List
//            LazyColumn(
//                modifier = Modifier.weight(1f),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(playlists) { (name, count) ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF4A5A9F))
//                            .clickable {
//                                Toast
//                                    .makeText(context, "Opening $name", Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                            .padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_playlist_play_24),
//                            contentDescription = name,
//                            tint = Color.White,
//                            modifier = Modifier.size(48.dp)
//                        )
//                        Spacer(modifier = Modifier.width(16.dp))
//                        Column(modifier = Modifier.weight(1f)) {
//                            Text(
//                                text = name,
//                                color = Color.White,
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                            Text(
//                                text = "$count songs",
//                                color = Color.White.copy(alpha = 0.7f),
//                                fontSize = 14.sp
//                            )
//                        }
//                    }
//                }
//            }
//
//            // Bottom Player Bar
//            MiniPlayerBar(
//                isPlaying = isPlaying,
//                onPlayPauseClick = {
//                    isPlaying = !isPlaying
//                },
//                onLikeClick = { },
//                onPreviousClick = { },
//                onNextClick = { }
//            )
//
//            // Bottom Navigation Bar
//            BottomNavigationBar(
//                selectedItem = selectedNavItem,
//                onItemClick = onNavItemClick
//            )
//        }
//    }
//}