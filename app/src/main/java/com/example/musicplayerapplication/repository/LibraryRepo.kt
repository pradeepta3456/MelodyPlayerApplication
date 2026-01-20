package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.model.MusicGenre
import com.example.musicplayerapplication.model.Song


interface LibraryRepo {
    fun getArtists(): List<LibraryArtist>
    fun getSongs(): List<Song>
    fun getAlbums(): List<Album>
    fun getGenres(): List<MusicGenre>
    fun getFolders(): List<String>
    fun scanDevice(): List<Song>
    fun searchArtists(query: String): List<LibraryArtist>
}
