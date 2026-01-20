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
            Song(1, "Ocean Eyes", "Billie Eilish", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Focean_eyes.jpg?alt=media", 0, "Don't Smile", "3:12"),
            Song(2, "Sunrise", "Heat Waves", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fsunrise.jpg?alt=media", 0, "Morning", "3:45"),
            Song(3, "Bite Me", "Risern", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fbite_me.jpg?alt=media", 0, "Dark Night", "4:20")
        )
        return allSongs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true)
        }
    }

    private fun searchArtists(query: String): List<Artist> {
        val allArtists = listOf(
            Artist(1, "Billie Eilish", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fbillie_eilish.jpg?alt=media", 0),
            Artist(2, "Heat Waves", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fheat_waves.jpg?alt=media", 0),
            Artist(3, "Risern", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Frisern.jpg?alt=media", 0)
        )
        return allArtists.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    private fun searchAlbums(query: String): List<Album> {
        val allAlbums = listOf(
            Album(1, "Don't Smile", "Billie Eilish", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Fdont_smile.jpg?alt=media"),
            Album(2, "Morning", "Heat Waves", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/albums%2Fmorning.jpg?alt=media")
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
            Song(1, "Ocean Eyes", "Billie Eilish", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Focean_eyes.jpg?alt=media", 1570000),
            Song(2, "Sunrise", "Heat Waves", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fsunrise.jpg?alt=media", 3650000),
            Song(3, "Bite Me", "Risern", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fbite_me.jpg?alt=media", 15000000)
        )
    }
}
