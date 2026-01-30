package com.example.musicplayerapplication.repository
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.model.MusicGenre
import com.example.musicplayerapplication.model.Song

class LibraryRepoImpl : LibraryRepo {

    // All data now comes from Firebase via MusicViewModel
    override fun getArtists(): List<LibraryArtist> = emptyList()

    override fun getSongs(): List<Song> = emptyList()

    override fun getAlbums(): List<Album> = emptyList()

    override fun getGenres(): List<MusicGenre> = emptyList()

    override fun getFolders(): List<String> = emptyList()

    override fun scanDevice(): List<Song> = emptyList()

    override fun searchArtists(query: String): List<LibraryArtist> = emptyList()
}
