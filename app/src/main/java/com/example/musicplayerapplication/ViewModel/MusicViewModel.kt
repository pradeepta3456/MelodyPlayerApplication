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
import com.example.musicplayerapplication.repository.ProfileRepository
import com.example.musicplayerapplication.repository.ProfileRepositoryImpl
import com.example.musicplayerapplication.repository.UserRepository
import com.example.musicplayerapplication.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MusicViewModel(
    private val context: Context,
    private val repository: MusicRepository = MusicRepoImpl(context),
    private val userRepository: UserRepository = UserRepositoryImpl(),
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl()
) : ViewModel() {

    private lateinit var audioPlayer: AudioPlayer

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var songStartTime: Long = 0

    // Playback state - must be initialized before init block
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentPlaylist = MutableStateFlow<List<Song>>(emptyList())
    val currentPlaylist: StateFlow<List<Song>> = _currentPlaylist

    init {
        // Initialize AudioPlayer here since the context is now available through the factory.
        audioPlayer = AudioPlayer.getInstance(context)
        loadAllSongs()

        // Observe AudioPlayer state and update playback state
        viewModelScope.launch {
            audioPlayer.isPlaying.collect { playing ->
                _playbackState.update { it.copy(isPlaying = playing) }
            }
        }

        viewModelScope.launch {
            audioPlayer.currentPosition.collect { position ->
                _playbackState.update { it.copy(currentPosition = position) }
            }
        }

        viewModelScope.launch {
            audioPlayer.currentSongFlow.collect { song ->
                song?.let {
                    _playbackState.update { state -> state.copy(currentSong = song) }
                }
            }
        }
    }

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
                    // Song not in playlist, use all songs as playlist
                    val allSongsList = _allSongsInternal.value
                    val songIndex = allSongsList.indexOfFirst { it.id == song.id }

                    if (songIndex != -1) {
                        _currentPlaylist.value = allSongsList
                        _playbackState.update {
                            it.copy(
                                currentSong = song,
                                queue = allSongsList,
                                currentIndex = songIndex,
                                isPlaying = true
                            )
                        }
                        // Set playlist in AudioPlayer
                        audioPlayer.setPlaylist(allSongsList, songIndex)
                    } else {
                        // Song not found, just play it alone
                        _currentPlaylist.value = listOf(song)
                        _playbackState.update {
                            it.copy(
                                currentSong = song,
                                queue = listOf(song),
                                currentIndex = 0,
                                isPlaying = true
                            )
                        }
                        audioPlayer.playSong(song)
                    }
                } else {
                    _playbackState.update {
                        it.copy(
                            currentSong = song,
                            currentIndex = index,
                            isPlaying = true
                        )
                    }
                    // Set playlist in AudioPlayer
                    audioPlayer.setPlaylist(_currentPlaylist.value, index)
                }

                // Save to recently played
                auth.currentUser?.uid?.let { userId ->
                    repository.addToRecentlyPlayed(userId, song)
                }

                // Track song start time for statistics
                songStartTime = System.currentTimeMillis()
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error playing song", e)
            }
        }
    }

    /**
     * Track song play completion for profile statistics
     */
    private fun trackSongCompletion(song: Song) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val durationPlayed = System.currentTimeMillis() - songStartTime

                // Only track if song was played for at least 30 seconds
                if (durationPlayed >= 30000) {
                    profileRepository.trackSongPlay(
                        userId = userId,
                        songId = song.id,
                        songTitle = song.title,
                        artist = song.artist,
                        durationPlayed = durationPlayed
                    )
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error tracking song play", e)
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
        // Track current song before skipping
        _playbackState.value.currentSong?.let { song ->
            trackSongCompletion(song)
        }

        val currentIndex = _playbackState.value.currentIndex
        val queue = _playbackState.value.queue
        if (queue.isNotEmpty() && currentIndex < queue.size - 1) {
            playSong(queue[currentIndex + 1])
        }
    }

    // Skip to previous song
    fun skipToPrevious() {
        // Track current song before skipping
        _playbackState.value.currentSong?.let { song ->
            trackSongCompletion(song)
        }

        audioPlayer.skipToPrevious()
    }

    // Seek to position
    fun seekTo(position: Long) {
        audioPlayer.seekTo(position)
    }

    // Toggle shuffle
    fun toggleShuffle() {
        audioPlayer.toggleShuffle()
    }

    // Toggle repeat mode
    fun toggleRepeatMode() {
        audioPlayer.toggleRepeatMode()
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
