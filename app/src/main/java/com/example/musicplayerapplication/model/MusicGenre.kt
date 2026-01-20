package com.example.musicplayerapplication.model
enum class MusicGenre(val displayName: String) {
    POP("Pop"),
    ROCK("Rock"),
    HIP_HOP("Hip Hop"),
    JAZZ("Jazz"),
    CLASSICAL("Classical"),
    ELECTRONIC("Electronic"),
    RNB("R&B"),
    COUNTRY("Country"),
    INDIE("Indie"),
    METAL("Metal"),
    BLUES("Blues"),
    REGGAE("Reggae"),
    LATIN("Latin"),
    KPOP("K-Pop"),
    BOLLYWOOD("Bollywood"),
    OTHER("Other");

    companion object {
        fun fromString(genre: String): MusicGenre {
            return values().find { it.displayName.equals(genre, ignoreCase = true) } ?: OTHER
        }

        fun getAllGenres(): List<String> {
            return values().map { it.displayName }
        }
    }
}