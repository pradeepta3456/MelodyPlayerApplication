package com.example.musicplayerapplication.View

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicplayerapplication.model.*
import com.example.musicplayerapplication.ViewModel.ProfileViewModelFactory
import com.example.musicplayerapplication.ViewModel.ProfileViewModel

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    val userStats by profileViewModel.userStats.collectAsState()
    val topSongs by profileViewModel.topSongs.collectAsState()
    val topArtists by profileViewModel.topArtists.collectAsState()
    val achievements by profileViewModel.achievements.collectAsState()
    val weeklyPattern by profileViewModel.weeklyPattern.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    val cardColor = Color(0xFF2D1B4E)
    val highlightColor = Color(0xFFE91E63)

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF21133B)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = highlightColor)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF21133B))
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    profile = userProfile,
                    formattedMemberSince = profileViewModel.getFormattedMemberSince()
                )
            }

            // Stats Row
            item {
                StatsSection(
                    stats = userStats,
                    formattedListeningTime = profileViewModel.getFormattedListeningTime(),
                    cardColor = cardColor
                )
            }

            // ARTIST MODE SECTION
            item {
                ArtistModeCard(cardColor = cardColor, highlightColor = highlightColor)
            }

            // TOP SONGS
            if (topSongs.isNotEmpty()) {
                item {
                    ProfileSectionHeader("Your Top Songs", Icons.Default.TrendingUp)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(topSongs) { song ->
                    TopSongItemNew(song, topSongs.indexOf(song) + 1)
                }
            } else {
                item {
                    EmptyStateCard("No listening history yet", "Start playing songs to see your top tracks!")
                }
            }

            // TOP ARTISTS
            if (topArtists.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    ProfileSectionHeader("Your Top Artists", Icons.Default.PersonAdd)
                    Spacer(modifier = Modifier.height(12.dp))
                    TopArtistsGridNew(topArtists)
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // ACHIEVEMENTS
            item {
                ProfileSectionHeader("Achievements", Icons.Default.EmojiEvents)
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(achievements) { achievement ->
                AchievementItemNew(achievement, cardColor, highlightColor)
            }

            // LISTENING PATTERN
            if (weeklyPattern.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    ProfileSectionHeader("Listening Pattern", Icons.Default.BarChart)
                    Spacer(modifier = Modifier.height(12.dp))
                    WeeklyPatternChart(weeklyPattern, cardColor, highlightColor)
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(profile: UserProfile?, formattedMemberSince: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Profile Picture
        if (profile?.profileImageUrl?.isNotEmpty() == true) {
            AsyncImage(
                model = profile.profileImageUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(45.dp))
            )
        } else {
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
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            profile?.displayName ?: "Music Lover",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            formattedMemberSince,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatsSection(stats: UserStats?, formattedListeningTime: String, cardColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                value = formattedListeningTime,
                label = "Listening Time",
                icon = Icons.Default.AccessTime,
                color = cardColor,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "${stats?.songsPlayed ?: 0}",
                label = "Songs Played",
                icon = Icons.Default.MusicNote,
                color = cardColor,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                value = stats?.topGenre ?: "Unknown",
                label = "Top Genre",
                icon = Icons.Default.Headset,
                color = cardColor,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "${stats?.dayStreak ?: 0}",
                label = "Day Streak",
                icon = Icons.Default.LocalFireDepartment,
                color = cardColor,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TopSongItemNew(song: TopSong, rank: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$rank",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Album Art
            if (song.coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = song.title,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
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

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(song.artist, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
            Text(
                "${song.playCount} plays",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TopArtistsGridNew(artists: List<TopArtist>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            artists.take(2).forEach { artist -> ArtistCardNew(artist, Modifier.weight(1f)) }
        }
        if (artists.size > 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                artists.drop(2).take(2).forEach { artist -> ArtistCardNew(artist, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun ArtistCardNew(artist: TopArtist, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(150.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0xFF6B4FA0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = artist.artistName,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(artist.artistName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                "${artist.playCount} plays",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AchievementItemNew(achievement: Achievement, cardColor: Color, highlightColor: Color) {
    val containerColor = if (achievement.isCompleted) cardColor else cardColor.copy(alpha = 0.5f)
    val checkmarkColor = if (achievement.isCompleted) highlightColor else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    achievement.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    achievement.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                if (!achievement.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { achievement.progress.toFloat() / achievement.target.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = highlightColor,
                        trackColor = Color.Gray.copy(alpha = 0.3f),
                    )
                    Text(
                        "${achievement.progress}/${achievement.target}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            }
            if (achievement.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = checkmarkColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
fun WeeklyPatternChart(weeklyPattern: List<WeeklyPattern>, cardColor: Color, highlightColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("Weekly Activity", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            val maxTime = weeklyPattern.maxOfOrNull { it.listeningTime } ?: 1

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                val days = listOf("S", "M", "T", "W", "T", "F", "S")
                weeklyPattern.forEachIndexed { index, pattern ->
                    val heightFraction = if (maxTime > 0) pattern.listeningTime.toFloat() / maxTime.toFloat() else 0f
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightFraction.coerceAtLeast(0.1f))
                                .width(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(highlightColor)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(days[pattern.dayOfWeek], color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(title: String, message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                message,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
    }
}
