// repository/LibraryRepositoryImpl.kt
package com.example.melodyplay.repository

import com.example.melodyplay.R
import com.example.melodyplay.model.LibraryArtist

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
