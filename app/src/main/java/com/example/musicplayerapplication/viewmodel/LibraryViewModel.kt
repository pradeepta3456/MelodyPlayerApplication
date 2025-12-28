package com.example.musicplayerapplication.viewmodel

import com.example.musicplayerapplication.model.LibraryArtist
import com.example.musicplayerapplication.repository.LibraryRepo

class LibraryViewModel(repository: LibraryRepo) : viewmodel() {

    val selectedCategory = mutableStateOf("Albums")
    val artists = mutableStateListOf<LibraryArtist>()

    init {
        artists.addAll(repository.getArtists())
    }

    fun selectCategory(category: String) {
        selectedCategory.value = category
        // Optional: filter artists if needed
    }
}

