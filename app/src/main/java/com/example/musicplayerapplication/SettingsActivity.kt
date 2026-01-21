package com.example.musicplayerapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen()
        }
    }
}

@Composable
fun SettingsScreen() {
    val backgroundColor = Color(0xFF7A4AA5)

    var selectedTabIndex by remember { mutableIntStateOf(1) }
    var selectedAppearanceIndex by remember { mutableIntStateOf(1) }
    var dynamicThemeEnabled by remember { mutableStateOf(true) }
    var selectedBottomNavIndex by remember { mutableIntStateOf(4) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                SegmentedControl(
                    items = listOf("Audio", "Theme", "General"),
                    selectedIndex = selectedTabIndex,
                    onItemSelected = { selectedTabIndex = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppearanceSection(
                    selectedIndex = selectedAppearanceIndex,
                    onSelectedChange = { selectedAppearanceIndex = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Dynamic Theme",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                DynamicThemeCard(
                    checked = dynamicThemeEnabled,
                    onCheckedChange = { dynamicThemeEnabled = it }
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            BottomNavBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                selectedIndex = selectedBottomNavIndex,
                onItemSelected = { selectedBottomNavIndex = it }
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", R.drawable.baseline_home_24),
        BottomNavItem("Library", R.drawable.baseline_library_music_24),
        BottomNavItem("Playlist", R.drawable.baseline_playlist_add_24),
        BottomNavItem("Profile", R.drawable.baseline_person_24),
        BottomNavItem("Settings", R.drawable.baseline_settings_24)
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            color = Color(0xFF8C56C0),
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFFFFF59D) else Color.White.copy(alpha = 0.85f),
                        label = "bottomNavColor"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onItemSelected(index) }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(if (isSelected) 24.dp else 22.dp),
                            tint = contentColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            color = contentColor,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

private data class BottomNavItem(
    val label: String,
    @DrawableRes val iconRes: Int
)

@Composable
private fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val background = Color(0xFF925CC0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                val containerColor by animateColorAsState(
                    if (isSelected) Color.White else Color.Transparent,
                    label = ""
                )
                val textColor by animateColorAsState(
                    if (isSelected) background else Color.White.copy(alpha = 0.85f),
                    label = ""
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .background(containerColor, RoundedCornerShape(20.dp))
                        .clickable { onItemSelected(index) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = textColor,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AppearanceSection(
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit
) {
    Column {
        Text(
            text = "🎨 Appearance",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppearanceCard(
                modifier = Modifier.weight(1f),
                title = "Light",
                iconRes = R.drawable.lightmode,
                selected = selectedIndex == 0,
                onClick = { onSelectedChange(0) }
            )
            AppearanceCard(
                modifier = Modifier.weight(1f),
                title = "Dark",
                iconRes = R.drawable.baseline_dark_mode_24,
                selected = selectedIndex == 1,
                onClick = { onSelectedChange(1) }
            )
            AppearanceCard(
                modifier = Modifier.weight(1f),
                title = "Auto",
                iconRes = R.drawable.baseline_dark_mode_24,
                selected = selectedIndex == 2,
                onClick = { onSelectedChange(2) }
            )
        }
    }
}

@Composable
private fun AppearanceCard(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val baseColor = Color(0xFF8C56C0)
    val selectedGradient = Brush.verticalGradient(
        listOf(Color(0xFFE0D0FF), Color(0xFFC29BFF))
    )

    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color.Transparent else baseColor.copy(alpha = 0.6f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (selected) selectedGradient else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DynamicThemeCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8C56C0).copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Album Art Colors",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Theme adapts to album artwork",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF7A4AA5)
@Composable
private fun SettingsPreview() {
    SettingsScreen()
}
