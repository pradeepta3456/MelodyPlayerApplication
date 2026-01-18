package com.example.musicplayerapplication
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.ui.theme.DarkPurpleBackground
import com.example.musicplayerapplication.view.HomeViewModel



class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    data class NavItem(val label: String, val icon: Int)

    var selectedIndex by remember { mutableStateOf(0) }
    var showNotificationScreen by remember { mutableStateOf(false) }

    val listItem = listOf(
        NavItem(label = "Home", icon = R.drawable.baseline_home_24),
        NavItem(label = "Library", icon = R.drawable.baseline_library_music_24),
        NavItem(label = "Playlist", icon = R.drawable.baseline_music_note_24),
        NavItem(label = "Profile", icon = R.drawable.baseline_person_24),
        NavItem(label = "Settings", icon = R.drawable.baseline_settings_24),
    )

    Scaffold(
        containerColor = DarkPurpleBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF6B21A8)
            ) {
                listItem.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label, fontSize = 12.sp) },
                        onClick = {
                            selectedIndex = index
                            showNotificationScreen = false // Close notification screen when navigating
                        },
                        selected = selectedIndex == index,
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
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
                .background(DarkPurpleBackground)
                .padding(padding)
        ) {
            if (showNotificationScreen) {
                NotificationScreen(
                    onBackClick = { showNotificationScreen = false }
                )
            } else {
            when (selectedIndex) {
                    0 -> HomeScreen(
                        viewModel = viewModel<HomeViewModel>(),
                        onNotificationClick = { showNotificationScreen = true },
                        onSearchClick = { /* TODO: Implement search functionality */ }
                    )
                1 -> LibraryScreen()
                2 -> PlaylistScreen()
                3 -> ProfileScreen()
                4 -> SettingsScreen()
                    else -> HomeScreen(
                        viewModel = viewModel<HomeViewModel>(),
                        onNotificationClick = { showNotificationScreen = true },
                        onSearchClick = { /* TODO: Implement search functionality */ }
                    )
                }
            }
        }
    }
}



