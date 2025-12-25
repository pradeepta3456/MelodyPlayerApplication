package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.R

class LibraryRepoImpl : LibraryRepo {
    override fun getArtists(): List<LibraryArtist> {
        return listOf(
            LibraryArtist("Luna Eclipse", 15, 2, R.drawable.img_1),
            LibraryArtist("Sunshine", 20, 3, R.drawable.img_2),
            LibraryArtist("Poster Girl", 10, 1, R.drawable.img_3),
            LibraryArtist("Disco Drive", 30, 4, R.drawable.img_4)
        )
    }
}