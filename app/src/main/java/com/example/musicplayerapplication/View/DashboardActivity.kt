package com.example.musicplayerapplication.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayerapplication.Utils.CloudinaryHelper
import com.example.musicplayerapplication.ViewModel.HomeViewModel
import com.example.musicplayerapplication.ViewModel.ProfileViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Cloudinary if not already initialized
        if (!CloudinaryHelper.isInitialized()) {
            try {
                CloudinaryHelper.initialize(
                    context = applicationContext,
                    cloudName = "drfit5xud",
                    apiKey = "649351633944394",
                    apiSecret = "dOKyZ9LYkoLKpkgP1zGs0oitL_k"
                )
                Log.d("DashboardActivity", "Cloudinary initialized successfully")
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Failed to initialize Cloudinary", e)
            }
        }

        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@Composable
fun DashboardBody() {
    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(0) }
    var showNotificationScreen by remember { mutableStateOf(false) }

    data class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
    val listItem = listOf(
        NavItem(label = "Home", icon = Icons.Default.Home),
        NavItem(label = "Library", icon = Icons.Default.LibraryMusic),
        NavItem(label = "Playlist", icon = Icons.Default.MusicNote),
        NavItem(label = "Profile", icon = Icons.Default.Person)
    )

    Scaffold(
        containerColor = Color(0xFF21133B),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddMusicActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = Color(0xFF8B5CF6)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Music",
                    tint = Color.White
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF6B21A8)
            ) {
                listItem.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label, fontSize = 12.sp) },
                        onClick = {
                            selectedIndex = index
                            showNotificationScreen = false
                        },
                        selected = selectedIndex == index && !showNotificationScreen,
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF21133B))
                .padding(padding)
        ) {
            if (showNotificationScreen) {
                // Call your existing NotificationScreen
                NotificationScreen(
                    onBackClick = { showNotificationScreen = false }
                )
            } else {
                when (selectedIndex) {
                    0 -> {
                        // Call your existing HomeScreen with exact parameters it expects
                        HomeScreen(
                            onNotificationClick = { showNotificationScreen = true },
                            onSearchClick = { }
                        )
                    }
                    1 -> {
                        // Call your existing LibraryScreen (it takes no parameters)
                        LibraryScreen()
                    }
                    2 -> {
                        // Call your existing PlaylistScreen (it takes no parameters)
                        PlaylistScreen()
                    }
                    3 -> {
                        // Call your existing ProfileScreen with the parameter it expects
                        ProfileScreen(profileViewModel = viewModel<ProfileViewModel>())
                    }
                }
            }
        }
    }
}