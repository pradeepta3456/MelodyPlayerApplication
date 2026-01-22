package com.example.musicplayerapplication.Utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.musicplayerapplication.model.RepeatMode
import com.example.musicplayerapplication.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioPlayer private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: AudioPlayer? = null

        fun getInstance(context: Context): AudioPlayer {
            return instance ?: synchronized(this) {
                instance ?: AudioPlayer(context.applicationContext).also { instance = it }
            }
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var currentSong: Song? = null
    private var playlist: List<Song> = emptyList()
    private var currentIndex: Int = 0

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _currentSongFlow = MutableStateFlow<Song?>(null)
    val currentSongFlow: StateFlow<Song?> = _currentSongFlow

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled

    private var onCompletionListener: (() -> Unit)? = null

    fun playSong(song: Song) {
        try {
            release()

            currentSong = song
            _currentSongFlow.value = song

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                setDataSource(song.audioUrl)
                prepareAsync()

                setOnPreparedListener {
                    start()
                    _isPlaying.value = true
                    _duration.value = it.duration.toLong()
                    updateProgress()
                }

                setOnCompletionListener {
                    handleCompletion()
                }

                setOnErrorListener { _, what, extra ->
                    _isPlaying.value = false
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun playPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
                updateProgress()
            }
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
                updateProgress()
            }
        }
    }

    fun seekTo(position: Long) {
        mediaPlayer?.let {
            it.seekTo(position.toInt())
            _currentPosition.value = position
        }
    }

    fun skipToNext() {
        if (playlist.isEmpty()) return

        currentIndex = when {
            _shuffleEnabled.value -> playlist.indices.random()
            currentIndex < playlist.size - 1 -> currentIndex + 1
            else -> 0
        }

        playSong(playlist[currentIndex])
    }

    fun skipToPrevious() {
        if (playlist.isEmpty()) return

        // If playing for more than 3 seconds, restart current song
        if (_currentPosition.value > 3000) {
            seekTo(0)
            return
        }

        currentIndex = when {
            _shuffleEnabled.value -> playlist.indices.random()
            currentIndex > 0 -> currentIndex - 1
            else -> playlist.size - 1
        }

        playSong(playlist[currentIndex])
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        playlist = songs
        currentIndex = startIndex
        if (songs.isNotEmpty()) {
            playSong(songs[startIndex])
        }
    }

    fun toggleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun toggleShuffle() {
        _shuffleEnabled.value = !_shuffleEnabled.value
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    fun getCurrentSong(): Song? = currentSong

    fun getDuration(): Long = mediaPlayer?.duration?.toLong() ?: 0L

    fun getCurrentPosition(): Long = mediaPlayer?.currentPosition?.toLong() ?: 0L

    private fun updateProgress() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                _currentPosition.value = player.currentPosition.toLong()
                _duration.value = player.duration.toLong()

                // Schedule next update
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    updateProgress()
                }, 100)
            }
        }
    }

    private fun handleCompletion() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                seekTo(0)
                resume()
            }
            RepeatMode.ALL -> {
                skipToNext()
            }
            RepeatMode.OFF -> {
                if (currentIndex < playlist.size - 1) {
                    skipToNext()
                } else {
                    _isPlaying.value = false
                }
            }
        }
        onCompletionListener?.invoke()
    }

    fun release() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            mediaPlayer = null
            _isPlaying.value = false
            _currentPosition.value = 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }
}
