package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.R
import com.example.musicplayerapplication.model.Album
import com.example.musicplayerapplication.model.Song

class HomeRepoImpl : HomeRepo {
    override fun getRecentSongs(): List<Song> = listOf(
        Song(1, "Starlight", "The Luminaries", R.drawable.img_1, 271),
        Song(2, "Moonlight Sonata", "Beethoven", R.drawable.img_2, 180),
        Song(3, "Sunset Drive", "Synthwave", R.drawable.img_3, 95),
        Song(4, "Eclipse", "Aurora", R.drawable.img_4, 120)
    )

    override fun getTrendingAlbums(): List<Album> = listOf(
        Album("Night Vibes", "Chill", R.drawable.img_5),
        Album("Fire Beats", "Hip Hop", R.drawable.img_6),
        Album("Synthwave Dreams", "Electronic", R.drawable.img_7),
        Album("Acoustic Moods", "Acoustic", R.drawable.img_5)
    )
}
