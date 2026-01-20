package com.example.musicplayerapplication.repository

import Artist
import androidx.compose.ui.R
import com.example.musicplayerapplication.model.Playlist
import com.example.musicplayerapplication.model.SearchResult
import com.example.musicplayerapplication.model.SearchSongs
import com.example.musicplayerapplication.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
class SearchRepoImpl : SearchRepository {

    private val recentSearches = mutableListOf<String>()

    override fun search(query: String): SearchResult {
        // Perform search across all content types
        val songs = searchSongs(query)
        val artists = searchArtists(query)
        val albums = searchAlbums(query)
        val playlists = searchPlaylists(query)

        return SearchResult(songs, artists, albums, playlists)
    }

    private fun searchSongs(query: String): List<Song> {
        val allSongs = listOf(
            Song(1, "Ocean Eyes", "Billie Eilish", R.drawable.img_1, 0, "Don't Smile", "3:12"),
            Song(2, "Sunrise", "Heat Waves", R.drawable.img_2, 0, "Morning", "3:45"),
            Song(3, "Bite Me", "Risern", R.drawable.img_3, 0, "Dark Night", "4:20")
        )
        return allSongs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true)
        }
    }

    private fun searchArtists(query: String): List<Artist> {
        val allArtists = listOf(
            Artist(1, "Billie Eilish", R.drawable.img_1, 0),
            Artist(2, "Heat Waves", R.drawable.img_2, 0),
            Artist(3, "Risern", R.drawable.img_3, 0)
        )
        return allArtists.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    private fun searchAlbums(query: String): List<Album> {
        val allAlbums = listOf(
            Album(1, "Don't Smile", "Billie Eilish", R.drawable.img_1),
            Album(2, "Morning", "Heat Waves", R.drawable.img_2)
        )
        return allAlbums.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }

    private fun searchPlaylists(query: String): List<Playlist> {
        return emptyList() // Implement playlist search
    }

    override fun getRecentSearches(): List<String> = recentSearches

    override fun saveRecentSearch(query: String) {
        if (!recentSearches.contains(query)) {
            recentSearches.add(0, query)
            if (recentSearches.size > 10) {
                recentSearches.removeLast()
            }
        }
    }

    override fun clearRecentSearches() {
        recentSearches.clear()
    }

    override fun getTrendingSongs(): List<Song> {
        return listOf(
            Song(1, "Ocean Eyes", "Billie Eilish", R.drawable.img_1, 0, plays = 1570000),
            Song(2, "Sunrise", "Heat Waves", R.drawable.img_2, 0, plays = 3650000),
            Song(3, "Bite Me", "Risern", R.drawable.img_3, 0, plays = 15000000)
        )
    }
}
