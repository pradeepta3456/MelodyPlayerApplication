package com.example.musicplayerapplication.repository

import Artist
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Playlist
import com.example.musicplayerapplication.model.SearchResult
import com.example.musicplayerapplication.model.SearchSongs
import com.example.musicplayerapplication.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
class SearchRepoImpl : SearchRepository {

    private val recentSearches = mutableListOf<String>()

    override fun search(query: String): SearchResult { val songs = searchSongs(query)
        val artists = searchArtists(query)
        val albums = searchAlbums(query)
        val playlists = searchPlaylists(query)

        return SearchResult(songs, artists, albums, playlists)
    }

    private fun searchSongs(query: String): List<Song> {
        val allSongs = listOf(
            Song(id = "1", title = "Ocean Eyes", artist = "Billie Eilish", coverUrl = "", plays = 0, album = "Don't Smile", durationFormatted = "3:12"),
            Song(id = "2", title = "Sunrise", artist = "Heat Waves", coverUrl = "", plays = 0, album = "Morning", durationFormatted = "3:45"),
            Song(id = "3", title = "Bite Me", artist = "Risern", coverUrl = "", plays = 0, album = "Dark Night", durationFormatted = "4:20")
        )
        return allSongs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true)
        }
    }

    private fun searchArtists(query: String): List<Artist> {
        val allArtists = listOf(
            Artist(1, "Billie Eilish", "", 0),
            Artist(2, "Heat Waves", "", 0),
            Artist(3, "Risern", "", 0)
        )
        return allArtists.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    private fun searchAlbums(query: String): List<Album> {
        val allAlbums = listOf(
            Album(1, "Don't Smile", "Billie Eilish", ""),
            Album(2, "Morning", "Heat Waves", "")
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
            Song(id = "1", title = "Ocean Eyes", artist = "Billie Eilish", coverUrl = "", plays = 1570000),
            Song(id = "2", title = "Sunrise", artist = "Heat Waves", coverUrl = "", plays = 3650000),
            Song(id = "3", title = "Bite Me", artist = "Risern", coverUrl = "", plays = 15000000)
        )
    }
}
