package com.example.melodyplay.repositoryimpl

import com.example.melodyplay.R
import com.example.melodyplay.model.*
import com.example.melodyplay.repository.ProfileRepo

class ProfileRepoImpl : ProfileRepo {

    override fun getTopSongs(): List<Song> {
        return listOf(
            Song("Summertime Sadness", "Lana Del Rey", R.drawable.img_2, 271),
            Song("Rockstar", "Future", R.drawable.img_7, 130),
            Song("Peace of Mind", "Bianca", R.drawable.img_6, 60),
            Song("Midnight Dreams", "Lightsbright", R.drawable.img_1, 100)
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
            Achievement("Music Explorer", "Played 1000+ songs", R.drawable.baseline_music_note_24, true),
            Achievement("Night Owl", "Listened past midnight 50 times", R.drawable.baseline_schedule_24, true),
            Achievement("Genre Master", "Explored 10+ genres", R.drawable.baseline_theater_comedy_24, true),
            Achievement("Playlist Creator", "Created 10 playlists", R.drawable.baseline_queue_music_24, false)
        )
    }
}
