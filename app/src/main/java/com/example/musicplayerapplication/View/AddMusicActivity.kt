package com.example.musicplayerapplication.View

import android.app.Activity
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicplayerapplication.Utils.CloudinaryHelper
import com.example.musicplayerapplication.ViewModel.MusicViewModel
import com.example.musicplayerapplication.model.Song

class AddMusicActivity : ComponentActivity() {
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
                Log.d("AddMusicActivity", "Cloudinary initialized successfully")
            } catch (e: Exception) {
                Log.e("AddMusicActivity", "Failed to initialize Cloudinary", e)
            }
        }

        setContent {
            AddMusicScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMusicScreen() {
    val context = LocalContext.current
    val viewModel = remember { MusicViewModel(context) }

    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var album by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var lyrics by remember { mutableStateOf("") }

    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var audioFileName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(0L) }
    var fileSize by remember { mutableStateOf(0L) }

    val uploadProgress by viewModel.uploadProgress
    val isUploading by viewModel.isUploading

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                audioUri = uri

                // Extract metadata
                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(context, uri)

                    // Get duration
                    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    duration = durationStr?.toLongOrNull() ?: 0L

                    // Auto-fill if empty
                    if (title.isEmpty()) {
                        title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
                    }
                    if (artist.isEmpty()) {
                        artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
                    }
                    if (album.isEmpty()) {
                        album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
                    }
                    if (genre.isEmpty()) {
                        genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""
                    }
                    if (year.isEmpty()) {
                        year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: ""
                    }

                    retriever.release()

                    // Get file name and size
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                        cursor.moveToFirst()
                        audioFileName = cursor.getString(nameIndex)
                        fileSize = cursor.getLong(sizeIndex)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error reading audio file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                coverUri = uri
            }
        }
    }

    fun uploadSong() {
        when {
            audioUri == null -> {
                Toast.makeText(context, "Please select an audio file", Toast.LENGTH_SHORT).show()
            }
            title.isEmpty() -> {
                Toast.makeText(context, "Please enter song title", Toast.LENGTH_SHORT).show()
            }
            artist.isEmpty() -> {
                Toast.makeText(context, "Please enter artist name", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val song = Song(
                    title = title,
                    artist = artist,
                    album = album,
                    genre = genre,
                    year = year.toIntOrNull() ?: 0,
                    duration = duration,
                    durationFormatted = Song.formatDuration(duration),
                    fileSize = fileSize,
                    lyrics = lyrics
                )

                viewModel.uploadSong(audioUri!!, coverUri, song)
            }
        }
    }

    // Monitor upload completion
    LaunchedEffect(isUploading) {
        if (!isUploading && uploadProgress == 1f) {
            Toast.makeText(context, "Song uploaded successfully!", Toast.LENGTH_LONG).show()
            (context as? ComponentActivity)?.finish()
        }
    }

    // Monitor errors
    val errorMsg by viewModel.errorMessage
    LaunchedEffect(errorMsg) {
        errorMsg?.let { error ->
            Toast.makeText(context, "Upload failed: $error", Toast.LENGTH_LONG).show()
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        "Add Music",
                        fontSize = 20.sp,
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
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Cover Image Upload
                Text(
                    "Cover Image",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2A3E))
                        .border(2.dp, Color(0xFF8B5CF6), RoundedCornerShape(12.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "image/*"
                            }
                            imagePickerLauncher.launch(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (coverUri != null) {
                        AsyncImage(
                            model = coverUri,
                            contentDescription = "Cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = "Upload Cover",
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF8B5CF6)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tap to select cover image",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Audio File Upload
                Text(
                    "Audio File",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "audio/*"
                            }
                            audioPickerLauncher.launch(intent)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A3E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = "Audio",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (audioFileName.isNotEmpty()) audioFileName else "Select audio file",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            if (duration > 0) {
                                Text(
                                    "${Song.formatDuration(duration)} • ${Song.formatFileSize(fileSize)}",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Icon(
                            Icons.Default.Upload,
                            contentDescription = "Upload",
                            tint = Color(0xFF8B5CF6)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Song Details
                Text(
                    "Song Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                MusicTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title *",
                    icon = Icons.Default.MusicNote
                )

                Spacer(modifier = Modifier.height(12.dp))

                MusicTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = "Artist *",
                    icon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(12.dp))

                MusicTextField(
                    value = album,
                    onValueChange = { album = it },
                    label = "Album",
                    icon = Icons.Default.Album
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MusicTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = "Genre",
                            icon = Icons.Default.MusicNote
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MusicTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = "Year",
                            icon = Icons.Default.CalendarToday
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = lyrics,
                    onValueChange = { lyrics = it },
                    label = { Text("Lyrics (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF4A4A5E),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color(0xFF8B5CF6),
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Upload Button
                Button(
                    onClick = { uploadSong() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Uploading... ${(uploadProgress * 100).toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudUpload, "Upload")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Upload Song",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MusicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = Color(0xFF8B5CF6))
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF8B5CF6),
            unfocusedBorderColor = Color(0xFF4A4A5E),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedLabelColor = Color(0xFF8B5CF6),
            unfocusedLabelColor = Color.Gray
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}
