package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.model.Achievement
import com.example.musicplayerapplication.model.Artist
import com.example.musicplayerapplication.model.Song

class ProfileRepoImpl : ProfileRepo {

    override fun getTopSongs(): List<Song> {
        return listOf(
            Song(1, "Summertime Sadness", "Lana Del Rey", R.drawable.img_2, plays = 271),
            Song(2, "Rockstar", "Future", R.drawable.img_7, plays = 130),
            Song(3, "Peace of Mind", "Bianca", R.drawable.img_6, plays = 60),
            Song(4, "Midnight Dreams", "Lightsbright", R.drawable.img_1, plays = 100)
        )
    }

    override fun getTopArtists(): List<Artist> {
        return listOf(
            Artist("Luna Eclipse", R.drawable.img_2, 250),
            Artist("Sunshine", R.drawable.img_7, 180),
            Artist("Poster Girl", R.drawable.img_6, 90),
            Artist("Disco Drive", R.drawable.img_7, 220)
        )
    }

    override fun getAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                "Music Explorer",
                "Played 1000+ songs",
                R.drawable.baseline_music_note_24,
                true
            ),
            Achievement(
                "Night Owl",
                "Listened past midnight 50 times",
                R.drawable.baseline_schedule_24,
                true
            ),
            Achievement(
                "Genre Master",
                "Explored 10+ genres",
                R.drawable.baseline_theater_comedy_24,
                true
            ),
            Achievement(
                "Playlist Creator",
                "Created 10 playlists",
                R.drawable.baseline_queue_music_24,
                false
            )
        )
    }
}


