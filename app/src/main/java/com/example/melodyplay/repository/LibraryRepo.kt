package com.example.melodyplay.repository

import com.example.melodyplay.model.LibraryArtist

interface LibraryRepo {
    fun getArtists(): List<LibraryArtist>
}