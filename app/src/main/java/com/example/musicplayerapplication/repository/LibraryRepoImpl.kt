package com.example.musicplayerapplication.repository
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.model.MusicGenre
import com.example.musicplayerapplication.model.Song

class LibraryRepoImpl : LibraryRepo {

    private val sampleArtists = listOf(
        LibraryArtist(1, "Luna Eclipse", 15, 2, "", "Electronic"),
        LibraryArtist(2, "Sunshine", 20, 3, "", "Pop"),
        LibraryArtist(3, "Poster Girl", 10, 1, "", "Rock"),
        LibraryArtist(4, "Disco Drive", 30, 4, "", "Disco")
    )

    private val sampleSongs = listOf(
        Song(id = "1", title = "Katsee", artist = "Luna Eclipse", coverUrl = "", plays = 0, album = "Luna Eclipse", durationFormatted = "2:50"),
        Song(id = "2", title = "Miline", artist = "Luna Eclipse", coverUrl = "", plays = 0, album = "Luna Eclipse", durationFormatted = "3:15"),
        Song(id = "3", title = "Star", artist = "Luna Eclipse", coverUrl = "", plays = 0, album = "Luna Eclipse", durationFormatted = "4:20"),
        Song(id = "4", title = "Eclipse", artist = "Luna Eclipse", coverUrl = "", plays = 0, album = "Luna Eclipse", durationFormatted = "3:45"),
        Song(id = "5", title = "Moon", artist = "Luna Eclipse", coverUrl = "", plays = 0, album = "Luna Eclipse", durationFormatted = "5:10")
    )

    override fun getArtists(): List<LibraryArtist> = sampleArtists

    override fun getSongs(): List<Song> = sampleSongs

    override fun getAlbums(): List<Album> {
        return listOf(
            Album(1, "Luna Eclipse", "Electronic Vibes", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Fluna_eclipse.jpg?alt=media", "", 5, 2024),
            Album(2, "Sunshine", "Pop Collection", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Fsunshine.jpg?alt=media", "", 8, 2023)
        )
    }

    override fun getGenres(): List<MusicGenre> {
        return listOf(
            MusicGenre(1, "pop", "Pop", 0xFFFFE066, 45),
            MusicGenre(2, "rock", "Rock", 0xFF8EFF8B, 32),
            MusicGenre(3, "hiphop", "Hip Hop", 0xFFFF5A47, 28),
            MusicGenre(4, "jazz", "Jazz", 0xFF4A2A24, 15)
        )
    }

    override fun getFolders(): List<String> {
        return listOf("Downloads", "Music", "Recordings", "WhatsApp Audio")
    }

    override fun scanDevice(): List<Song> {
        // Scan device using MediaStore
        // Return list of found songs
        return sampleSongs
    }

    override fun searchArtists(query: String): List<LibraryArtist> {
        return sampleArtists.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }
}
