package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.SearchResult
import com.example.musicplayerapplication.model.SearchSongs
import com.example.musicplayerapplication.model.Song
import kotlinx.coroutines.flow.Flow


interface SearchRepository {
    fun search(query: String): SearchResult
    fun getRecentSearches(): List<String>
    fun saveRecentSearch(query: String)
    fun clearRecentSearches()
    fun getTrendingSongs(): List<Song>
}

