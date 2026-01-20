package com.example.musicplayerapplication.repository

import Artist
import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.model.User

class ProfileRepoImpl : ProfileRepo {

    override fun getTopSongs(): List<Song> {
        return listOf(
            Song(1, "Summertime Sadness", "Lana Del Rey", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fsummertime_sadness.jpg?alt=media", 271, "Paradise", "4:25"),
            Song(2, "Rockstar", "Future", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Frockstar.jpg?alt=media", 130, "HNDRXX", "3:50"),
            Song(3, "Peace of Mind", "Bianca", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fpeace_of_mind.jpg?alt=media", 60, "Silence", "3:15"),
            Song(4, "Midnight Dreams", "Lightsbright", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/songs%2Fmidnight_dreams.jpg?alt=media", 100, "Night Sky", "4:10")
        )
    }

    override fun getTopArtists(): List<Artist> {
        return listOf(
            Artist(1, "Luna Eclipse", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fluna_eclipse.jpg?alt=media", 250, 15, 2),
            Artist(2, "Sunshine", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fsunshine.jpg?alt=media", 180, 20, 3),
            Artist(3, "Poster Girl", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fposter_girl.jpg?alt=media", 90, 10, 1),
            Artist(4, "Disco Drive", "https://firebasestorage.googleapis.com/v0/b/chillvibes-e80df.firebasestorage.app/o/artists%2Fdisco_drive.jpg?alt=media", 220, 30, 4)
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
