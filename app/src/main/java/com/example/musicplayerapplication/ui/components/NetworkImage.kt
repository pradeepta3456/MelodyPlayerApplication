package com.example.musicplayerapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

/**
 * Network image loader component using Coil
 * Displays images from Firebase Storage URLs with loading and error states
 */
@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    cornerRadius: Dp = 8.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius)),
        contentScale = contentScale,
        loading = {
            Box(
                modifier = Modifier
                    .size(size)
                    .background(Color(0xFF1E1E1E)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFFFF6B4A)
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .size(size)
                    .background(Color(0xFF1E1E1E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Placeholder",
                    tint = Color(0xFFFF6B4A),
                    modifier = Modifier.size(size / 2)
                )
            }
        }
    )
}

/**
 * Circular network image for artist/profile pictures
 */
@Composable
fun CircularNetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    NetworkImage(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier,
        size = size,
        cornerRadius = size / 2, // Makes it circular
        contentScale = ContentScale.Crop
    )
}

/**
 * Album/Song cover image with square aspect ratio
 */
@Composable
fun AlbumCoverImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    cornerRadius: Dp = 8.dp
) {
    NetworkImage(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier,
        size = size,
        cornerRadius = cornerRadius,
        contentScale = ContentScale.Crop
    )
}
