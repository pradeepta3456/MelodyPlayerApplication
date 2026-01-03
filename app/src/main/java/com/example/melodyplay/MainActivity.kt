package com.example.melodyplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.NavigationBarItemDefaults

import com.example.melodyplay.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MelodyPlayTheme {
                MainActivityUI(profileViewModel)
            }
        }
    }
}

// Data class for BottomBar tabs
data class BottomBarItem(
    val label: String,
    val iconRes: Int
)


@Composable
fun BottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomBarItem("Home", R.drawable.baseline_home_24),
        BottomBarItem("Library", R.drawable.baseline_library_music_24),
        BottomBarItem("Playlists", R.drawable.baseline_playlist_add_24),
        BottomBarItem("Profile", R.drawable.baseline_person_24),
        BottomBarItem("Settings", R.drawable.baseline_settings_24)
    )
    NavigationBar(
        tonalElevation = 20.dp,
        containerColor = Color(0xA6331354),
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.LightGray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.LightGray
                )
            )
        }
    }
}

    @Composable
fun MainActivityUI(profileViewModel: ProfileViewModel) {

    var selectedTab by remember { mutableStateOf(0) }
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreenContent()
                1 -> MusicLibraryScreen(navController)
                2 -> PlaylistScreen()
                3 -> ProfileScreen(profileViewModel)
                4 -> SettingsScreen()
            }
        }
        }
    }
