package com.example.musicplayerapplication.View


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.musicplayerapplication.model.SearchSongs
import com.example.musicplayerapplication.ViewModel.SearchViewModel

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerTheme {
                SearchScreen()
            }
        }
    }
}

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel()
) {
    val query by viewModel.query.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background( Color(0xFF272F72))
            .padding(16.dp)
    ) {
        SearchTextField(
            query = query,
            onQueryChange = viewModel::onQueryChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (recentSearches.isNotEmpty()) {
            RecentSearches(
                searches = recentSearches,
                onSearchClick = viewModel::onRecentSearchClick
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        TrendingNow(songs)
    }
}


@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A3F9D), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2C3883),
                unfocusedContainerColor = Color(0xFF272F72),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.White
        )
    }
}




@Composable
fun RecentSearches(
    searches: List<String>,
    onSearchClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Recent Searches",
            color = Color.White,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            searches.forEach { search ->
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { onSearchClick(search) }
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(search, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

data class TrendingSong(
    val rank: String,
    val title: String,
    val artist: String,
    val plays: String
)

@Composable
fun TrendingNow(songs: List<SearchSongs>) {
    Column {
        Text(
            text = "Trending Now",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(songs) { song ->
                TrendingItem(
                    TrendingSong(
                        rank = song.id.toString(),
                        title = song.title,
                        artist = song.artist,
                        plays = song.plays
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun TrendingItem(song: TrendingSong) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.2f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = song.rank,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(song.artist, color = Color.White.copy(0.7f), fontSize = 12.sp)
        }

        Text(song.plays, color = Color.White, fontSize = 12.sp)
    }
}