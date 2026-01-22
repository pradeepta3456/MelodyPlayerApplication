package com.example.musicplayerapplication.ViewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.Utils.AudioPlayer
import com.example.musicplayerapplication.model.PlaybackState
import com.example.musicplayerapplication.model.RepeatMode
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.repository.MusicRepoImpl
import com.example.musicplayerapplication.repository.MusicRepository
import com.example.musicplayerapplication.repository.UserRepository
import com.example.musicplayerapplication.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MusicViewModel(
    private val context: Context,
    private val repository: MusicRepository = MusicRepoImpl(context),
    private val userRepository: UserRepository = UserRepositoryImpl()
) : ViewModel() {

    private lateinit var audioPlayer: AudioPlayer

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        // Initialize AudioPlayer here since the context is now available through the factory.
        audioPlayer = AudioPlayer.getInstance(context)
        loadAllSongs()
    }

    // Playback state
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentPlaylist = MutableStateFlow<List<Song>>(emptyList())
    val currentPlaylist: StateFlow<List<Song>> = _currentPlaylist

    // Upload state
    private val _uploadProgress = mutableStateOf(0f)
    val uploadProgress: State<Float> = _uploadProgress

    private val _isUploading = mutableStateOf(false)
    val isUploading: State<Boolean> = _isUploading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Songs
    private val _allSongsInternal = MutableStateFlow<List<Song>>(emptyList())

    val allSongs: StateFlow<List<Song>> = _allSongsInternal.combine(auth.currentUser?.uid?.let { userId ->
        flow { emit(repository.getFavoriteSongs(userId).getOrElse { emptyList() }) }
    } ?: flowOf(emptyList())) { all, favorites ->
        all.map { song -> song.copy(isFavorite = favorites.any { it.id == song.id }) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentlyPlayedSongs: StateFlow<List<Song>> = (auth.currentUser?.uid?.let { userId ->
        flow { emit(repository.getRecentlyPlayed(userId).getOrElse { emptyList() }) }
    } ?: flowOf(emptyList()))
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Load all songs from repository
    fun loadAllSongs() {
        viewModelScope.launch {
            try {
                val result = repository.getAllSongs()
                result.onSuccess { songs ->
                    _allSongsInternal.value = songs
                }.onFailure { error ->
                    Log.e("MusicViewModel", "Error loading songs", error)
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Exception loading songs", e)
            }
        }
    }

    // Set current playlist
    fun setPlaylist(songs: List<Song>) {
        _currentPlaylist.value = songs
        _playbackState.update { it.copy(queue = songs) }
    }

    // Play a song
    fun playSong(song: Song) {
        viewModelScope.launch {
            try {
                // Find the song index in the current playlist
                val index = _currentPlaylist.value.indexOfFirst { it.id == song.id }
                if (index == -1) {
                    // Song not in playlist, add it
                    _currentPlaylist.value = listOf(song)
                    _playbackState.update {
                        it.copy(
                            currentSong = song,
                            queue = listOf(song),
                            currentIndex = 0,
                            isPlaying = true
                        )
                    }
                } else {
                    _playbackState.update {
                        it.copy(
                            currentSong = song,
                            currentIndex = index,
                            isPlaying = true
                        )
                    }
                }

                // Play audio using AudioPlayer
                audioPlayer.playSong(song)

                // Save to recently played
                auth.currentUser?.uid?.let { userId ->
                    repository.addToRecentlyPlayed(userId, song)
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error playing song", e)
            }
        }
    }

    // Pause playback
    fun pause() {
        audioPlayer.pause()
        _playbackState.update { it.copy(isPlaying = false) }
    }

    // Resume playback
    fun resume() {
        audioPlayer.resume()
        _playbackState.update { it.copy(isPlaying = true) }
    }

    // Stop playback
    fun stop() {
        audioPlayer.pause()
        _playbackState.update { it.copy(isPlaying = false, currentPosition = 0) }
    }

    // Skip to next song
    fun skipToNext() {
        val currentIndex = _playbackState.value.currentIndex
        val queue = _playbackState.value.queue
        if (queue.isNotEmpty() && currentIndex < queue.size - 1) {
            playSong(queue[currentIndex + 1])
        }
    }

    // Skip to previous song
    fun skipToPrevious() {
        val currentIndex = _playbackState.value.currentIndex
        val queue = _playbackState.value.queue
        if (queue.isNotEmpty() && currentIndex > 0) {
            playSong(queue[currentIndex - 1])
        }
    }

    // Toggle favorite
    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                if (song.isFavorite) {
                    repository.removeFromFavorites(userId, song.id)
                } else {
                    repository.addToFavorites(userId, song)
                }
                // Reload songs to update favorite status
                loadAllSongs()
            }
        }
    }

    // Upload song
    fun uploadSong(audioUri: Uri, coverUri: Uri?, song: Song) {
        viewModelScope.launch {
            try {
                _isUploading.value = true
                _uploadProgress.value = 0f
                _errorMessage.value = null

                val userId = auth.currentUser?.uid
                if (userId == null) {
                    _errorMessage.value = "User not authenticated"
                    _isUploading.value = false
                    return@launch
                }

                // Upload to repository with progress callback
                val result = repository.uploadSong(
                    audioUri = audioUri,
                    coverUri = coverUri,
                    songDetails = song,
                    onProgress = { progress ->
                        _uploadProgress.value = progress
                    }
                )

                result.onSuccess {
                    _uploadProgress.value = 1f
                    _isUploading.value = false
                    // Reload songs to include the new upload
                    loadAllSongs()
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Upload failed"
                    _isUploading.value = false
                    _uploadProgress.value = 0f
                    Log.e("MusicViewModel", "Upload failed", error)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Upload failed"
                _isUploading.value = false
                _uploadProgress.value = 0f
                Log.e("MusicViewModel", "Upload exception", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
