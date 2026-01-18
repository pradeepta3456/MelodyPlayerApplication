package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.SearchSongs
import kotlinx.coroutines.flow.Flow


interface SearchRepository {



    fun getTrendingSongs(): Flow<List<SearchSongs>>

    fun searchSongs(query: String): Flow<List<SearchSongs>>




}
