package com.example.melodyplay.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.melodyplay.model.LibraryArtist
import com.example.melodyplay.repository.LibraryRepo

class LibraryViewModel(repository: LibraryRepo) : ViewModel() {

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

