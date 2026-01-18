package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.SearchSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
class SearchRepoImpl : SearchRepository {

    private val songs = MutableStateFlow(
        listOf(
            SearchSongs(
                id = 1,
                title = "Ocean Eyes",
                artist = "Billie Eilish",
                plays = "1.57 M"
            ),
            SearchSongs(
                id = 2,
                title = "Sunrise",
                artist = "Heat Waves",
                plays = "3.65 M"
            ),
            SearchSongs(
                id = 3,
                title = "Bite Me",
                artist = "Risern",
                plays = "15 M"
            )
        )
    )

    override fun getTrendingSongs(): Flow<List<SearchSongs>> {
        return songs
    }

    override fun searchSongs(query: String): Flow<List<SearchSongs>> {
        return songs.map { list ->
            if (query.isBlank()) list
            else list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true)
            }
        }
    }
}

