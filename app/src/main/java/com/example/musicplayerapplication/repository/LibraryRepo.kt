package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.LibraryArtist

interface LibraryRepo {
    fun getArtists(): List<LibraryArtist>
}