package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.HomeRepo
import com.example.musicplayerapplication.repository.HomeRepoImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel(
    private val homeRepository: HomeRepo = HomeRepoImpl(),
    private val musicViewModel: MusicViewModel
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Expose filtered and processed data from MusicViewModel
    val allSongs: StateFlow<List<Song>> = musicViewModel.allSongs

    val recentSongs: StateFlow<List<Song>> = musicViewModel.recentlyPlayedSongs
        .combine(allSongs) { recent, all ->
            if (recent.isNotEmpty()) {
                recent.take(10)
            } else {
                // If no recently played, show some all songs
                all.take(10)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val trendingAlbums: StateFlow<List<Album>> = allSongs.map {
        // Group songs by album and create dummy Album objects for now
        it.groupBy { song -> song.album }
            .map { (albumName, songsInAlbum) ->
                Album(
                    title = albumName.ifEmpty { "Unknown Album" },
                    artistVibes = songsInAlbum.firstOrNull()?.artist ?: "Unknown Artist",
                    coverUrl = songsInAlbum.firstOrNull()?.coverUrl.orEmpty()
                )
            }.shuffled().take(3) // Take a few trending albums
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Existing isLoading and errorMessage
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    init {
        // We rely on MusicViewModel to load all songs
        // musicViewModel.loadAllSongs() is called in MusicViewModel's init
    }

    fun playSong(song: Song) {
        musicViewModel.playSong(song)
    }

    fun playAlbum(album: Album) {
        val albumSongs = allSongs.value.filter { it.album == album.title }
        if (albumSongs.isNotEmpty()) {
            musicViewModel.setPlaylist(albumSongs)
            musicViewModel.playSong(albumSongs.first())
        } else {
            errorMessage.value = "No songs found for this album."
        }
    }

    fun clearError() {
        errorMessage.value = null
    }
}
