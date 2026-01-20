package com.example.musicplayerapplication.View

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.ui.theme.DarkPurpleBackground
import com.example.musicplayerapplication.ViewModel.*

/**
 * Main Dashboard Activity - Single Container for All Screens
 * Professional MVVM Pattern with proper state management
 */
class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    // Navigation state
    var selectedIndex by remember { mutableStateOf(0) }
    var showNotificationScreen by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showScanDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Navigation items
    data class NavItem(val label: String, val icon: Int)
    val navItems = listOf(
        NavItem("Home", R.drawable.baseline_home_24),
        NavItem("Library", R.drawable.baseline_library_music_24),
        NavItem("Playlist", R.drawable.baseline_music_note_24),
        NavItem("Profile", R.drawable.baseline_person_24),
        NavItem("Settings", R.drawable.baseline_settings_24)
    )

    Scaffold(
        containerColor = DarkPurpleBackground,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF6B21A8)) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label, fontSize = 12.sp) },
                        selected = selectedIndex == index && !showNotificationScreen,
                        onClick = {
                            selectedIndex = index
                            showNotificationScreen = false
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color(0xFF9C27B0),
                            unselectedTextColor = Color(0xFF9C27B0),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            // Show FAB only on Home and Library screens
            if (selectedIndex in listOf(0, 1, 2) && !showNotificationScreen) {
                FloatingActionButton(
                    onClick = { showUploadDialog = true },
                    containerColor = Color(0xFF8B5CF6),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Music",
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkPurpleBackground)
                .padding(padding)
        ) {
            when {
                showNotificationScreen -> {
                    NotificationScreen(
                        onBackClick = { showNotificationScreen = false }
                    )
                }
                else -> {
                    when (selectedIndex) {
                        0 -> HomeScreen(
                            viewModel = viewModel<HomeViewModel>(),
                            onNotificationClick = { showNotificationScreen = true },
                            onSearchClick = { /* Navigate to search */ },
                            onUploadClick = { showUploadDialog = true }
                        )
                        1 -> LibraryScreen(
                            onScanDeviceClick = { showScanDialog = true },
                            onUploadClick = { showUploadDialog = true }
                        )
                        2 -> PlaylistScreen(
                            onUploadClick = { showUploadDialog = true }
                        )
                        3 -> ProfileScreen(
                            profileViewModel = viewModel<ProfileViewModel>()
                        )
                        4 -> SettingsScreen()
                    }
                }
            }
        }
    }

    // Upload Music Dialog
    if (showUploadDialog) {
        UploadMusicDialog(
            onDismiss = { showUploadDialog = false },
            onUploadFromDevice = {
                // Handle device upload
                showUploadDialog = false
                Toast.makeText(context, "Select music from device", Toast.LENGTH_SHORT).show()
            },
            onUploadFromUrl = {
                // Handle URL upload
                showUploadDialog = false
                Toast.makeText(context, "Upload from URL", Toast.LENGTH_SHORT).show()
            },
            onRecordAudio = {
                // Handle audio recording
                showUploadDialog = false
                Toast.makeText(context, "Record audio", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Scan Device Dialog
    if (showScanDialog) {
        ScanDeviceDialog(
            onDismiss = { showScanDialog = false },
            onConfirm = {
                showScanDialog = false
                Toast.makeText(context, "Scanning device for music...", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Upload Music Dialog - Professional Options
 */
@Composable
fun UploadMusicDialog(
    onDismiss: () -> Unit,
    onUploadFromDevice: () -> Unit,
    onUploadFromUrl: () -> Unit,
    onRecordAudio: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2D1B4E),
        title = {
            Text(
                "Add Music",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UploadOption(
                    icon = R.drawable.baseline_folder_24,
                    title = "From Device",
                    description = "Select music files from your device",
                    onClick = onUploadFromDevice
                )

                UploadOption(
                    icon = R.drawable.baseline_link_24,
                    title = "From URL",
                    description = "Add music from a URL link",
                    onClick = onUploadFromUrl
                )

                UploadOption(
                    icon = R.drawable.baseline_mic_24,
                    title = "Record Audio",
                    description = "Record your own audio",
                    onClick = onRecordAudio
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

/**
 * Upload Option Card
 */
@Composable
fun UploadOption(
    icon: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3D2766)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                painter = painterResource(R.drawable.baseline_arrow_forward_24),
                contentDescription = "Go",
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Scan Device Dialog
 */
@Composable
fun ScanDeviceDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2D1B4E),
        icon = {
            Icon(
                painter = painterResource(R.drawable.baseline_search_24),
                contentDescription = "Scan",
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "Scan Device for Music",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                "This will scan your device's storage for audio files and add them to your library. This may take a few moments.",
                color = Color.White.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6)
                )
            ) {
                Text("Start Scan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}