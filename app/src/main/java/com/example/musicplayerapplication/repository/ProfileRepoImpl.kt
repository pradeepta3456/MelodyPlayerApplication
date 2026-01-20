package com.example.musicplayerapplication.repository

import Artist
import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Song
import com.example.musicplayerapplication.model.User

class ProfileRepoImpl : ProfileRepo {

    override fun getTopSongs(): List<Song> {
        return listOf(
            Song(1, "Summertime Sadness", "Lana Del Rey", R.drawable.img_2, 271, "Paradise", "4:25", coverResId = R.drawable.img_2),
            Song(2, "Rockstar", "Future", R.drawable.img_7, 130, "HNDRXX", "3:50", coverResId = R.drawable.img_7),
            Song(3, "Peace of Mind", "Bianca", R.drawable.img_6, 60, "Silence", "3:15", coverResId = R.drawable.img_6),
            Song(4, "Midnight Dreams", "Lightsbright", R.drawable.img_1, 100, "Night Sky", "4:10", coverResId = R.drawable.img_1)
        )
    }

    override fun getTopArtists(): List<Artist> {
        return listOf(
            Artist(1, "Luna Eclipse", R.drawable.img_2, 250, 15, 2),
            Artist(2, "Sunshine", R.drawable.img_7, 180, 20, 3),
            Artist(3, "Poster Girl", R.drawable.img_6, 90, 10, 1),
            Artist(4, "Disco Drive", R.drawable.img_7, 220, 30, 4)
        )
    }

    override fun getAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                1,
                "Music Explorer",
                "Played 1000+ songs",
                R.drawable.baseline_music_note_24,
                true,
                1000,
                1000
            ),
            Achievement(
                2,
                "Night Owl",
                "Listened past midnight 50 times",
                R.drawable.baseline_schedule_24,
                true,
                50,
                50
            ),
            Achievement(
                3,
                "Genre Master",
                "Explored 10+ genres",
                R.drawable.baseline_theater_comedy_24,
                true,
                10,
                10
            ),
            Achievement(
                4,
                "Playlist Creator",
                "Created 10 playlists",
                R.drawable.baseline_queue_music_24,
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
