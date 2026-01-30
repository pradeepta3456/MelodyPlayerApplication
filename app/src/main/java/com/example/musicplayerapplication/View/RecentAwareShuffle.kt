package com.example.musicplayerapplication.View

class RecentAwareShuffle(
    private val allSongs: List<String>,
    private val recentLimit: Int = 5
) {

    private val recent = ArrayDeque<String>()

    fun getNextSong(): String {
        val available = allSongs.filterNot { recent.contains(it) }
            .ifEmpty { allSongs }

        val next = available.random()
        recent.addLast(next)

        if (recent.size > recentLimit) {
            recent.removeFirst()
        }
        return next
    }
}

