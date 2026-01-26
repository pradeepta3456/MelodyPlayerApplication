package com.example.musicplayerapplication.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayerapplication.Utils.EqualizerPreset
import com.example.musicplayerapplication.ViewModel.AudioEffectsViewModel
import com.example.musicplayerapplication.ViewModel.AudioEffectsViewModelFactory

class AudioEffectsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AudioEffectsScreenContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioEffectsScreenContent() {
    val context = LocalContext.current
    val viewModel: AudioEffectsViewModel = viewModel(
        factory = AudioEffectsViewModelFactory(context)
    )

    val bassLevel by viewModel.bassLevel.collectAsState()
    val trebleLevel by viewModel.trebleLevel.collectAsState()
    val volumeLevel by viewModel.volumeLevel.collectAsState()
    val reverbEnabled by viewModel.reverbEnabled.collectAsState()
    val reverbLevel by viewModel.reverbLevel.collectAsState()
    val equalizerPreset by viewModel.equalizerPreset.collectAsState()
    val equalizerBands by viewModel.equalizerBands.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Audio Effects",
                            tint = Color(0xFF00D9FF),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Audio Effects",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Reset button
                    IconButton(onClick = { viewModel.resetAudioEffects() }) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset",
                            tint = Color(0xFFFF6B6B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Volume Control
                EffectCard(title = "Volume", icon = Icons.Default.VolumeUp) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Volume Level",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "${(volumeLevel * 100).toInt()}%",
                                color = Color(0xFF00D9FF),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomSlider(
                            value = volumeLevel,
                            onValueChange = { viewModel.updateVolumeLevel(it) },
                            valueRange = 0f..1f,
                            steps = 100,
                            color = Color(0xFF00D9FF)
                        )
                    }
                }

                // Bass Control
                EffectCard(title = "Bass", icon = Icons.Default.Speaker) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Bass Boost",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "${bassLevel.toInt()} dB",
                                color = Color(0xFFFF6B6B),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomSlider(
                            value = bassLevel,
                            onValueChange = { viewModel.updateBassLevel(it) },
                            valueRange = -10f..10f,
                            steps = 20,
                            color = Color(0xFFFF6B6B)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("-10 dB", color = Color.Gray, fontSize = 12.sp)
                            Text("+10 dB", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                // Treble Control
                EffectCard(title = "Treble", icon = Icons.Default.MusicNote) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Treble Boost",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "${trebleLevel.toInt()} dB",
                                color = Color(0xFF4ECDC4),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomSlider(
                            value = trebleLevel,
                            onValueChange = { viewModel.updateTrebleLevel(it) },
                            valueRange = -10f..10f,
                            steps = 20,
                            color = Color(0xFF4ECDC4)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("-10 dB", color = Color.Gray, fontSize = 12.sp)
                            Text("+10 dB", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                // Reverb Control
                EffectCard(title = "Reverb", icon = Icons.Default.Waves) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Reverb Effect",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Switch(
                                checked = reverbEnabled,
                                onCheckedChange = { viewModel.toggleReverb() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFFFD93D),
                                    checkedTrackColor = Color(0xFFFFD93D).copy(alpha = 0.5f),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )
                        }

                        if (reverbEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Intensity",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    "${reverbLevel.toInt()}%",
                                    color = Color(0xFFFFD93D),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            CustomSlider(
                                value = reverbLevel,
                                onValueChange = { viewModel.updateReverbLevel(it) },
                                valueRange = 0f..100f,
                                steps = 100,
                                color = Color(0xFFFFD93D)
                            )
                        }
                    }
                }

                // Equalizer Presets
                EffectCard(title = "Equalizer Presets", icon = Icons.Default.Equalizer) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PresetButton(
                                label = "Flat",
                                isSelected = equalizerPreset == "FLAT",
                                onClick = { viewModel.applyEqualizerPreset(EqualizerPreset.FLAT) },
                                modifier = Modifier.weight(1f)
                            )
                            PresetButton(
                                label = "Bass",
                                isSelected = equalizerPreset == "BASS_BOOST",
                                onClick = { viewModel.applyEqualizerPreset(EqualizerPreset.BASS_BOOST) },
                                modifier = Modifier.weight(1f)
                            )
                            PresetButton(
                                label = "Treble",
                                isSelected = equalizerPreset == "TREBLE_BOOST",
                                onClick = { viewModel.applyEqualizerPreset(EqualizerPreset.TREBLE_BOOST) },
                                modifier = Modifier.weight(1f)
                            )
                            PresetButton(
                                label = "Rock",
                                isSelected = equalizerPreset == "ROCK",
                                onClick = { viewModel.applyEqualizerPreset(EqualizerPreset.ROCK) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PresetButton(
                                label = "Pop",
                                isSelected = equalizerPreset == "POP",
                                onClick = { viewModel.applyEqualizerPreset(EqualizerPreset.POP) },
                                modifier = Modifier.weight(1f)
                            )
                            PresetButton(
                                label = "Jazz",
                                isSelected = equalizerPreset == "JAZZ",
                                onClick = { viewModel.applyEqualizerPreset(EqualizerPreset.JAZZ) },
                                modifier = Modifier.weight(1f)
                            )
                            PresetButton(
                                label = "Classical",
                                isSelected = equalizerPreset == "CLASSICAL",
                                onClick = { viewModel.applyEqualizerPreset(EqualizerPreset.CLASSICAL) },
                                modifier = Modifier.weight(1f)
                            )
                            PresetButton(
                                label = "Vocal",
                                isSelected = equalizerPreset == "VOCAL",
                                onClick = { viewModel.applyEqualizerPreset(EqualizerPreset.VOCAL) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 5-Band Equalizer
                EffectCard(title = "Custom Equalizer", icon = Icons.Default.Tune) {
                    Column {
                        Text(
                            "Current Preset: $equalizerPreset",
                            color = Color(0xFF00D9FF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val frequencies = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
                        equalizerBands.forEachIndexed { index, level ->
                            if (index < frequencies.size) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            frequencies[index],
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            "${level.toInt()} dB",
                                            color = Color(0xFF8B5CF6),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    CustomSlider(
                                        value = level,
                                        onValueChange = { newLevel ->
                                            viewModel.updateEqualizerBand(index, newLevel)
                                        },
                                        valueRange = -10f..10f,
                                        steps = 20,
                                        color = Color(0xFF8B5CF6)
                                    )
                                    if (index < equalizerBands.size - 1) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom spacing
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun EffectCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2A47).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF00D9FF),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            content()
        }
    }
}

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    color: Color
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        colors = SliderDefaults.colors(
            thumbColor = color,
            activeTrackColor = color,
            inactiveTrackColor = color.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PresetButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF00D9FF) else Color(0xFF2A3F5F),
            contentColor = if (isSelected) Color.Black else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
