package com.example.musicplayerapplication.repository
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.model.MusicGenre
import com.example.musicplayerapplication.model.Song

class LibraryRepoImpl : LibraryRepo {

    private val sampleArtists = listOf(
        LibraryArtist(1, "Luna Eclipse", 15, 2, "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fluna_eclipse.jpg?alt=media", "Electronic"),
        LibraryArtist(2, "Sunshine", 20, 3, "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fsunshine.jpg?alt=media", "Pop"),
        LibraryArtist(3, "Poster Girl", 10, 1, "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fposter_girl.jpg?alt=media", "Rock"),
        LibraryArtist(4, "Disco Drive", 30, 4, "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fdisco_drive.jpg?alt=media", "Disco")
    )

    private val sampleSongs = listOf(
        Song(1, "Katsee", "Luna Eclipse", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fkatsee.jpg?alt=media", 0, "Luna Eclipse", "2:50"),
        Song(2, "Miline", "Luna Eclipse", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fmiline.jpg?alt=media", 0, "Luna Eclipse", "3:15"),
        Song(3, "Star", "Luna Eclipse", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fstar.jpg?alt=media", 0, "Luna Eclipse", "4:20"),
        Song(4, "Eclipse", "Luna Eclipse", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Feclipse.jpg?alt=media", 0, "Luna Eclipse", "3:45"),
        Song(5, "Moon", "Luna Eclipse", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fmoon.jpg?alt=media", 0, "Luna Eclipse", "5:10")
    )

    override fun getArtists(): List<LibraryArtist> = sampleArtists

    override fun getSongs(): List<Song> = sampleSongs

    override fun getAlbums(): List<Album> {
        return listOf(
            Album(1, "Luna Eclipse", "Electronic Vibes", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Fluna_eclipse.jpg?alt=media", 5, 2024),
            Album(2, "Sunshine", "Pop Collection", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Fsunshine.jpg?alt=media", 8, 2023)
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
