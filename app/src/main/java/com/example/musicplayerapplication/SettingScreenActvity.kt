package com.example.musicplayerapplication


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.foundation.shape.CircleShape

import com.example.musicplayerapplication.AudioSettings
import com.example.musicplayerapplication.GeneralSettings
import com.example.musicplayerapplication.HomeScreen
import com.example.musicplayerapplication.LibraryScreen
import com.example.musicplayerapplication.PlaylistScreen
import com.example.musicplayerapplication.ProfileScreen
import com.example.musicplayerapplication.ThemeSettings


import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape



class SettingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsScreen()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    var selectedTab by remember { mutableStateOf(4) }
    val purpleBg = Color(0xFF834DCE)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        },
        containerColor = purpleBg
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            when (selectedTab) {

                2-> PlaylistScreenActivity()

                1 -> ProfileScreen()
                4 -> {
                    // REAL settings content UI
                    Text(
                        text = "Settings",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(20.dp))

                    SettingTabs()
                    Spacer(Modifier.height(25.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "", tint = Color.White)
                        Spacer(Modifier.width(10.dp))
                        Text("Equalizer", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(Modifier.height(10.dp))
                    EqualizerChips()
                    Spacer(Modifier.height(20.dp))

                    EqualizerSlider("Bass")
                    Spacer(Modifier.height(10.dp))

                    EqualizerSlider("Mid")
                    Spacer(Modifier.height(10.dp))

                    EqualizerSlider("Treble")
                    Spacer(Modifier.height(30.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SyncAlt, contentDescription = "", tint = Color.White)
                        Spacer(Modifier.width(10.dp))
                        Text("Crossfade", color = Color.White, fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(10.dp))

                    CrossfadeSlider()
                }
            }
        }
    }
}

@Composable
fun SettingTabs() {
    var selected by remember { mutableStateOf(0) }
    val titles = listOf("Audio", "Theme", "General")

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF9661D1), RoundedCornerShape(25.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            titles.forEachIndexed { index, text ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected == index) Color.White else Color.Transparent)
                        .clickable { selected = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = if (selected == index) Color.Black else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selected) {
            0 -> AudioSettings()
            1 -> ThemeSettings()
            2 -> GeneralSettings()
        }

    }
}



@Composable
fun EqualizerChips(){
    val options = listOf("balanced","Bass Boost", "Vocal", "Treble", "Custom")
    var selected by remember { mutableStateOf("balanced") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { item ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected == item) Color.White else Color(0xFF9661D1))
                    .clickable{ selected = item}
                    .padding(horizontal = 12.dp, vertical = 6.dp)

            ){
                Text(
                    text = item,
                    color = if (selected == item) Color.Black else Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerSlider(label: String) {
    var value by remember { mutableStateOf(0.5f) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White)
            Text("${(value * 10).toInt()}%", color = Color.White)
        }

        Slider(
            value = value,
            onValueChange = { value = it },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.Black,
                inactiveTrackColor = Color(0xFFE0E0E0)
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(4.dp),
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Black,
                        inactiveTrackColor = Color(0xFFE0E0E0)
                    )
                )
            }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrossfadeSlider() {
    var value by remember { mutableStateOf(0.5f) }

    Text("Duration", color = Color.White)

    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.Black,
            inactiveTrackColor = Color(0xFFE0E0E0)
        ),
        thumb = {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(4.dp),
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color(0xFFE0E0E0)
                )
            )
        }
    )
}




@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {

    val items = listOf(
        Icons.Default.Home to "Home",
        Icons.Default.LibraryMusic to "Library",
        Icons.Default.MusicNote to "Playlist",
        Icons.Default.Person to "Profile",
        Icons.Default.Settings to "Setting"
    )

    NavigationBar(
        containerColor = Color(0xFF9661D1)
    ) {
        items.forEachIndexed { index, (icon, label) ->

            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        icon,
                        contentDescription = "",
                        tint = Color.White
                    )
                },
                label = {
                    Text(label, color = Color.White)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettings() {
    SettingsScreen()
}


