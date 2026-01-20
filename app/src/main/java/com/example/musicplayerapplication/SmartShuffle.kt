package com.example.musicplayerapplication


class SmartShuffle(private val allSongs: List<String>) {

    private val shuffleQueue = mutableListOf<String>()
    private val recentHistory = ArrayDeque<String>()
    private val RECENT_LIMIT = 3

    fun getNextSong(): String {
        if (shuffleQueue.isEmpty()) {
            refillShuffleQueue()
        }

        val nextSong = shuffleQueue.removeAt(0)
        addToRecent(nextSong)
        return nextSong
    }

    private fun refillShuffleQueue() {
        shuffleQueue.clear()

        val filtered = allSongs.filterNot { recentHistory.contains(it) }
        shuffleQueue.addAll(filtered.shuffled())

        // If all songs were recently played, reset
        if (shuffleQueue.isEmpty()) {
            shuffleQueue.addAll(allSongs.shuffled())
        }
    }

    private fun addToRecent(song: String) {
        recentHistory.addLast(song)
        if (recentHistory.size > RECENT_LIMIT) {
            recentHistory.removeFirst()
        }
    }
}
