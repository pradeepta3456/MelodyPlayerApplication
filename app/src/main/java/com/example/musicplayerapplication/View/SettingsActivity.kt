package com.example.musicplayerapplication.View

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayerapplication.ViewModel.SettingsViewModel
import com.example.musicplayerapplication.ViewModel.SettingsViewModelFactory
import com.example.musicplayerapplication.model.AudioQuality
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val settings by viewModel.settings.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showDownloadQualityDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    // Show error snackbar if there's an error
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            // You can show a snackbar or toast here
            // For now, just clear the error after showing
            viewModel.clearError()
        }
    }

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
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
                // Playback Section
                SettingsSection(title = "Playback") {
                    SettingsItem(
                        icon = Icons.Default.MusicNote,
                        title = "Audio Quality",
                        subtitle = settings.audioQuality.name,
                        onClick = { showAudioQualityDialog = true }
                    )

                    SettingsSwitchItem(
                        icon = Icons.Default.PlayArrow,
                        title = "Gapless Playback",
                        subtitle = "Seamless playback between songs",
                        checked = settings.gaplessPlayback,
                        onCheckedChange = { viewModel.updateGaplessPlayback(it) }
                    )

                    SettingsSwitchItem(
                        icon = Icons.Default.Description,
                        title = "Show Lyrics",
                        subtitle = "Display lyrics when available",
                        checked = settings.showLyrics,
                        onCheckedChange = { viewModel.updateShowLyrics(it) }
                    )

                    SettingsSwitchItem(
                        icon = Icons.Default.Settings,
                        title = "Equalizer",
                        subtitle = "Enable audio equalizer",
                        checked = settings.enableEqualizer,
                        onCheckedChange = { viewModel.updateEnableEqualizer(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Download Section
                SettingsSection(title = "Downloads") {
                    SettingsItem(
                        icon = Icons.Default.Download,
                        title = "Download Quality",
                        subtitle = settings.downloadQuality.name,
                        onClick = { showDownloadQualityDialog = true }
                    )

                    SettingsSwitchItem(
                        icon = Icons.Default.Wifi,
                        title = "Stream on WiFi Only",
                        subtitle = "Save mobile data",
                        checked = settings.streamOnWifiOnly,
                        onCheckedChange = { viewModel.updateStreamOnWifiOnly(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Account Section
                SettingsSection(title = "Account") {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Profile",
                        subtitle = "View and edit your profile",
                        onClick = {
                            val intent = Intent(context, ProfileEditActivity::class.java)
                            context.startActivity(intent)
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Security,
                        title = "Privacy",
                        subtitle = "Manage your privacy settings",
                        onClick = {
                            val intent = Intent(context, PrivacySettingsActivity::class.java)
                            context.startActivity(intent)
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Manage notification preferences",
                        onClick = {
                            val intent = Intent(context, NotificationSettingsActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // About Section
                SettingsSection(title = "About") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "About App",
                        subtitle = "Version 1.0.0",
                        onClick = {
                            val intent = Intent(context, AboutActivity::class.java)
                            intent.putExtra("screen", "about")
                            context.startActivity(intent)
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        subtitle = "Read our terms",
                        onClick = {
                            val intent = Intent(context, AboutActivity::class.java)
                            intent.putExtra("screen", "terms")
                            context.startActivity(intent)
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        subtitle = "Read our privacy policy",
                        onClick = {
                            val intent = Intent(context, AboutActivity::class.java)
                            intent.putExtra("screen", "privacy")
                            context.startActivity(intent)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFDC2626).copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Logout",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Delete Account Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteAccountDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF991B1B).copy(alpha = 0.25f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = "Delete Account",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Delete Account",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            Text(
                                "Permanently delete your account and data",
                                fontSize = 13.sp,
                                color = Color(0xFFEF4444).copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Audio Quality Dialog
    if (showAudioQualityDialog) {
        AudioQualityDialog(
            currentQuality = settings.audioQuality,
            onDismiss = { showAudioQualityDialog = false },
            onSelect = { quality ->
                viewModel.updateAudioQuality(quality)
                showAudioQualityDialog = false
            }
        )
    }

    // Download Quality Dialog
    if (showDownloadQualityDialog) {
        AudioQualityDialog(
            currentQuality = settings.downloadQuality,
            onDismiss = { showDownloadQualityDialog = false },
            onSelect = { quality ->
                viewModel.updateDownloadQuality(quality)
                showDownloadQualityDialog = false
            }
        )
    }

    // Logout Dialog - Now using ViewModel (proper MVVM)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", color = Color.White) },
            text = { Text("Are you sure you want to logout?", color = Color.Gray) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout(
                        onSuccess = {
                            val intent = Intent(context, SignInActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        },
                        onError = { error ->
                            // Handle error - could show a toast
                            showLogoutDialog = false
                        }
                    )
                }) {
                    Text("Logout", color = Color(0xFFDC2626))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF8B5CF6))
                }
            },
            containerColor = Color(0xFF2A2A3E)
        )
    }

    // Delete Account Dialog - Now using ViewModel (proper MVVM)
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    "Delete Account?",
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "This action cannot be undone. All your data will be permanently deleted:",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("• All uploaded songs", color = Color.Gray, fontSize = 13.sp)
                    Text("• Playlists and favorites", color = Color.Gray, fontSize = 13.sp)
                    Text("• User profile and settings", color = Color.Gray, fontSize = 13.sp)
                    Text("• Play history", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Are you absolutely sure?",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount(
                            onSuccess = {
                                val intent = Intent(context, SignInActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            },
                            onError = { error ->
                                // Handle error - user might need to re-authenticate
                                showDeleteAccountDialog = false
                            }
                        )
                    }
                ) {
                    Text("Delete Forever", color = Color(0xFF991B1B), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = Color(0xFF8B5CF6))
                }
            },
            containerColor = Color(0xFF2A2A3E)
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
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
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}

@Composable
fun SettingsSwitchItem(
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

@Composable
fun AudioQualityDialog(
    currentQuality: AudioQuality,
    onDismiss: () -> Unit,
    onSelect: (AudioQuality) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Audio Quality", color = Color.White) },
        text = {
            Column {
                AudioQuality.entries.forEach { quality ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(quality) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = quality == currentQuality,
                            onClick = { onSelect(quality) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF8B5CF6),
                                unselectedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                quality.name,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Text(
                                "${quality.bitrate} kbps",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = Color(0xFF2A2A3E)
    )
}
