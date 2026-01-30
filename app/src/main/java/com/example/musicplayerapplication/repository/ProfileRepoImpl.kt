package com.example.musicplayerapplication.repository

import Artist
import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.model.User

class ProfileRepoImpl : ProfileRepo {

    override fun getTopSongs(): List<Song> {
        return listOf(
            Song(id = "1", title = "Summertime Sadness", artist = "Lana Del Rey", coverUrl = "", plays = 271, album = "Paradise", durationFormatted = "4:25"),
            Song(id = "2", title = "Rockstar", artist = "Future", coverUrl = "", plays = 130, album = "HNDRXX", durationFormatted = "3:50"),
            Song(id = "3", title = "Peace of Mind", artist = "Bianca", coverUrl = "", plays = 60, album = "Silence", durationFormatted = "3:15"),
            Song(id = "4", title = "Midnight Dreams", artist = "Lightsbright", coverUrl = "", plays = 100, album = "Night Sky", durationFormatted = "4:10")
        )
    }

    override fun getTopArtists(): List<Artist> {
        return listOf(
            Artist(1, "Luna Eclipse", "", 250, 15, 2),
            Artist(2, "Sunshine", "", 180, 20, 3),
            Artist(3, "Poster Girl", "", 90, 10, 1),
            Artist(4, "Disco Drive", "", 220, 30, 4)
        )
    }

    override fun getAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                1,
                "Music Explorer",
                "Played 1000+ songs",
                "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/icons%2Fmusic_note.png?alt=media",
                true,
                1000,
                1000
            ),
            Achievement(
                2,
                "Night Owl",
                "Listened past midnight 50 times",
                "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/icons%2Fschedule.png?alt=media",
                true,
                50,
                50
            ),
            Achievement(
                3,
                "Genre Master",
                "Explored 10+ genres",
                "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/icons%2Ftheater.png?alt=media",
                true,
                10,
                10
            ),
            Achievement(
                4,
                "Playlist Creator",
                "Created 10 playlists",
                "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/icons%2Fqueue_music.png?alt=media",
                false,
                7,
                10
            )
        )
    }

    override fun getUserProfile(): User {
        return User(
            id = "1",
            email = "user@example.com",
            displayName = "Music Lover",
            listeningTime = 4483200000, // 1247 hours in milliseconds
            songsPlayed = 3421,
            topGenre = "Electronic",
            dayStreak = 45
        )
    }

    override fun updateProfile(displayName: String, profileImageUrl: String) {
        // Update user profile in database
    }

    override fun getUserStats(): Map<String, Any> {
        return mapOf(
            "listeningTime" to "1247h",
            "songsPlayed" to 3421,
            "topGenre" to "Electronic",
            "dayStreak" to 45
        )
    }
}
