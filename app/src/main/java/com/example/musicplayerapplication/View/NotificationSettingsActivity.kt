package com.example.musicplayerapplication.View

import android.os.Bundle
import android.widget.Switch
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NotificationSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationSettingsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen() {
    val context = LocalContext.current

    var newReleases by remember { mutableStateOf(true) }
    var playlistUpdates by remember { mutableStateOf(true) }
    var socialActivity by remember { mutableStateOf(false) }
    var recommendations by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(false) }
    var pushNotifications by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E2E),
                        Color(0xFF0A0A0F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        "Notifications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Notification Types
                Text(
                    "Notification Types",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        NotificationToggleItem(
                            icon = Icons.Default.Album,
                            title = "New Releases",
                            subtitle = "Notify about new music from followed artists",
                            checked = newReleases,
                            onCheckedChange = { newReleases = it }
                        )

                        NotificationToggleItem(
                            icon = Icons.Default.PlaylistPlay,
                            title = "Playlist Updates",
                            subtitle = "Updates from your playlists",
                            checked = playlistUpdates,
                            onCheckedChange = { playlistUpdates = it }
                        )

                        NotificationToggleItem(
                            icon = Icons.Default.People,
                            title = "Social Activity",
                            subtitle = "What your friends are listening to",
                            checked = socialActivity,
                            onCheckedChange = { socialActivity = it }
                        )

                        NotificationToggleItem(
                            icon = Icons.Default.Recommend,
                            title = "Recommendations",
                            subtitle = "Personalized music suggestions",
                            checked = recommendations,
                            onCheckedChange = { recommendations = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notification Channels
                Text(
                    "Notification Channels",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        NotificationToggleItem(
                            icon = Icons.Default.PhoneAndroid,
                            title = "Push Notifications",
                            subtitle = "Receive notifications on this device",
                            checked = pushNotifications,
                            onCheckedChange = { pushNotifications = it }
                        )

                        NotificationToggleItem(
                            icon = Icons.Default.Email,
                            title = "Email Notifications",
                            subtitle = "Receive notifications via email",
                            checked = emailNotifications,
                            onCheckedChange = { emailNotifications = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF8B5CF6),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF4A4A5E)
            )
        )
    }
}
