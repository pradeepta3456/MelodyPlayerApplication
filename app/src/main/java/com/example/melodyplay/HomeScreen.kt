package com.example.melodyplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.melodyplay.model.Song
import com.example.melodyplay.ui.theme.MelodyPlayTheme


// ---------------------- COLORS ----------------------
val DarkPurpleBackground = Color(0xFF1A0E2E)
val CardBackground = Color(0xFF2D1B4E)
val AccentPink = Color(0xFFE91E63)
val TextYellow = Color(0xFFE9D200)

// ---------------------- DATA MODEL ----------------------


// ---------------------- SAMPLE DATA ----------------------
val sampleRecentSongs = listOf(
    Song("Starlight", "The Luminaries", R.drawable.img_1, 271),
    Song("Moonlight Sonata", "Beethoven", R.drawable.img_2, 271),
    Song("Sunset Drive", "Synthwave", R.drawable.img_3, 271),
    Song("Eclipse", "Aurora", R.drawable.img_4, 271)
)
val sampleTrending = listOf(
    Album("Night Vibes", "Chill", R.drawable.img_5),
    Album("Fire Beats", "Hip Hop", R.drawable.img_6),
    Album("Synthwave Dreams", "Electronic", R.drawable.img_7),
    Album("Acoustic Moods", "Acoustic", R.drawable.img_8)
)
data class Album(
    val title: String,
    val artistVibes: String,
    val imageRes: Int
)

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MelodyPlayTheme {
                HomeScreenContent()
            }
        }
    }
}


// ---------------------- HOME SCREEN ----------------------
@Composable
fun HomeScreenContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkPurpleBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Welcome Header ---
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Welcome to", color = Color.White.copy(alpha = 0.8f), fontSize = 20.sp)
                Text("Melody", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- Featured Album ---
        item {
            FeaturedAlbumCard(
                albumTitle = "Lunar Eclipse",
                artistVibes = "Sunset Vibes",
                imageRes = R.drawable.img_4
            )
        }

        // --- Recently Played Header ---
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_pause_24),
                    contentDescription = "Currently Playing Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Recently Played",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // --- Recently Played List ---
        items(sampleRecentSongs) { song ->
            RecentSongItem(song = song)
        }

        // --- Trending Header ---
        item {
            Text(
                "Trending",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // --- Trending Albums Horizontal List ---
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(sampleTrending) { album ->
                    TrendingCard(album)
                }
            }
        }


    }
}




// ---------------------- FEATURED ALBUM CARD ----------------------
@Composable
fun FeaturedAlbumCard(albumTitle: String, artistVibes: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Column: Text & Button
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Featured Album", color = TextYellow, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text(
                    albumTitle,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    artistVibes,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = { /* Handle Play */ },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                        contentDescription = "Play",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Now", fontWeight = FontWeight.SemiBold)
                }
            }

            // Right Column: Album Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = albumTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            )
        }
    }
}

// ---------------------- RECENT SONG ITEM ----------------------
@Composable
fun RecentSongItem(song: Song) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Image and Text
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = song.imageRes),
                    contentDescription = song.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = song.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = song.artist,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }

            // Right: Play Icon
            Icon(
                painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
@Composable
fun TrendingCard(album: Album) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = album.imageRes),
                contentDescription = album.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    album.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    album.artistVibes,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}


// ---------------------- PREVIEW ----------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MusicPlayerUIPreview() {
    MelodyPlayTheme {
        HomeScreenContent()
    }
}