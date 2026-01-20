package com.example.musicplayerapplication




import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Artist
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.ui.theme.MusicPlayerApplicationTheme
import com.example.musicplayerapplication.view.ProfileViewModel



class ProfileActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerApplicationTheme() {
                ProfileScreen(profileViewModel)
            }
        }
    }
}







// ---------------------- PROFILE SCREEN ----------------------
@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = ProfileViewModel()) {
    val topSongs = profileViewModel.topSongs
    val topArtists = profileViewModel.topArtists
    val achievements = profileViewModel.achievements


    val cardColor = Color(0xFF2D1B4E)
    val highlightColor = Color(0xFFE91E63)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF21133B))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 32.dp, bottom = 16.dp)
    ) {

        // Profile Header
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Music Lover", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Member since Nov 2024", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("1247h", "Listening Time", R.drawable.baseline_access_time_filled_24_2_2, cardColor, Modifier.weight(1f))
                StatCard("3421", "Songs Played", R.drawable.baseline_music_note_24, cardColor, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Electronic", "Top Genre", R.drawable.baseline_headset_24, cardColor, Modifier.weight(1f))
                StatCard("45", "Day Streak", R.drawable.baseline_local_fire_department_24, cardColor, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // TOP SONGS
        item {
            ProfileSectionHeader("Your Top Songs", R.drawable.baseline_trending_up_24)
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(topSongs) { song ->
            TopSongItem(song, topSongs.indexOf(song) + 1)
        }

        // TOP ARTISTS
        item {
            Spacer(modifier = Modifier.height(32.dp))
            ProfileSectionHeader("Your Top Artist", R.drawable.baseline_person_add_alt_1_24)
            Spacer(modifier = Modifier.height(12.dp))
            TopArtistsGrid(topArtists)
            Spacer(modifier = Modifier.height(32.dp))
        }

        // ACHIEVEMENTS
        item {
            ProfileSectionHeader("Achievements", R.drawable.baseline_emoji_events_24)
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(achievements) { achievement ->
            AchievementItem(achievement, cardColor, highlightColor)
        }

        // LISTENING PATTERN
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Listening Pattern",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            ListeningPatternPlaceholder(cardColor)
        }
    }
}

// ---------------------- HELPER COMPOSABLES ----------------------
@Composable
fun ProfileSectionHeader(title: String, iconRes: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatCard(value: String, label: String, iconRes: Int, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(100.dp), colors = CardDefaults.cardColors(containerColor = color), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun TopSongItem(song: Song, rank: Int) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("$rank", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                val coverId = song.coverResId ?: R.drawable.baseline_library_music_24

                Image(
                    painter = painterResource(id = coverId),
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(song.artist, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
            Text("${song.plays} plays", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun TopArtistsGrid(artists: List<Artist>) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            artists.take(2).forEach { artist -> ArtistCard(artist, Modifier.weight(1f)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            artists.drop(2).take(2).forEach { artist -> ArtistCard(artist, Modifier.weight(1f)) }
        }
    }
}

@Composable
fun ArtistCard(artist: Artist, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(150.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(30.dp))) {
                Image(painter = painterResource(id = artist.imageRes), contentDescription = artist.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(artist.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("${artist.plays} plays", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement, cardColor: Color, highlightColor: Color) {
    val containerColor = if (achievement.isCompleted) cardColor else cardColor.copy(alpha = 0.5f)
    val checkmarkColor = if (achievement.isCompleted) highlightColor else Color.Gray

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = containerColor), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.Black.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = achievement.iconRes), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(achievement.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(achievement.description, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            if (achievement.isCompleted) {
                Icon(painter = painterResource(id = R.drawable.baseline_check_circle_24), contentDescription = "Completed", tint = checkmarkColor, modifier = Modifier.size(24.dp))
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
fun ListeningPatternPlaceholder(cardColor: Color) {
    Card(modifier = Modifier.fillMaxWidth().height(150.dp), colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("Activity Graph Placeholder", color = Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom) {
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                val heights = listOf(0.4f, 0.7f, 0.9f, 0.6f, 0.5f, 0.8f, 0.3f)
                days.forEachIndexed { index, day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight(heights[index]).width(12.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFE91E63)))
                        Text(day, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview() {
    val previewViewModel = ProfileViewModel()
    MusicPlayerApplicationTheme() {
        ProfileScreen(profileViewModel = previewViewModel)
    }
}