package com.example.musicplayerapplication.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PrivacySettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrivacySettingsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen() {
    val context = LocalContext.current

    var profileVisibility by remember { mutableStateOf(true) }
    var showListeningHistory by remember { mutableStateOf(true) }
    var allowDataCollection by remember { mutableStateOf(false) }
    var shareActivity by remember { mutableStateOf(false) }

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
                        "Privacy",
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
                Text(
                    "Privacy Settings",
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
                        PrivacyToggleItem(
                            title = "Public Profile",
                            subtitle = "Let others see your profile",
                            checked = profileVisibility,
                            onCheckedChange = { profileVisibility = it }
                        )

                        PrivacyToggleItem(
                            title = "Show Listening History",
                            subtitle = "Display what you've been listening to",
                            checked = showListeningHistory,
                            onCheckedChange = { showListeningHistory = it }
                        )

                        PrivacyToggleItem(
                            title = "Data Collection",
                            subtitle = "Allow anonymous usage data collection",
                            checked = allowDataCollection,
                            onCheckedChange = { allowDataCollection = it }
                        )

                        PrivacyToggleItem(
                            title = "Share Activity",
                            subtitle = "Share your activity with friends",
                            checked = shareActivity,
                            onCheckedChange = { shareActivity = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "About Privacy",
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
                    Text(
                        "We take your privacy seriously. Your data is encrypted and secure. " +
                        "You can control what information is visible to others and what data " +
                        "we collect to improve your experience.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacyToggleItem(
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
            Icons.Default.Security,
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
