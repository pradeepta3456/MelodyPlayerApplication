package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.*
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

/**
 * Firebase implementation of ProfileRepository
 * Database structure:
 * /users/{userId}/
 *   - profile: UserProfile
 *   - stats: UserStats
 *   - listeningHistory/{timestamp}: ListeningHistoryEntry
 *   - achievements/{achievementId}: Achievement
 */
class ProfileRepositoryImpl : ProfileRepository {

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    override suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val snapshot = usersRef.child(userId).child("profile").get().await()
            snapshot.getValue(UserProfile::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateUserProfile(userId: String, profile: UserProfile): Boolean {
        return try {
            usersRef.child(userId).child("profile").setValue(profile).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getUserStats(userId: String): UserStats? {
        return try {
            val snapshot = usersRef.child(userId).child("stats").get().await()
            snapshot.getValue(UserStats::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateUserStats(userId: String, stats: UserStats): Boolean {
        return try {
            usersRef.child(userId).child("stats").setValue(stats).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getTopSongs(userId: String, limit: Int): List<TopSong> {
        return try {
            // Get listening history
            val historySnapshot = usersRef.child(userId).child("listeningHistory")
                .orderByChild("timestamp")
                .limitToLast(1000) // Get last 1000 plays
                .get()
                .await()

            // Count plays per song
            val songPlayCounts = mutableMapOf<String, MutableMap<String, Any>>()

            historySnapshot.children.forEach { entry ->
                val songId = entry.child("songId").value as? String ?: return@forEach
                val title = entry.child("songTitle").value as? String ?: ""
                val artist = entry.child("artist").value as? String ?: ""

                if (songPlayCounts.containsKey(songId)) {
                    val songData = songPlayCounts[songId]!!
                    songData["count"] = (songData["count"] as Int) + 1
                } else {
                    songPlayCounts[songId] = mutableMapOf(
                        "title" to title,
                        "artist" to artist,
                        "count" to 1
                    )
                }
            }

            // Get song details from songs database
            val songsSnapshot = database.getReference("songs").get().await()

            // Convert to TopSong list and sort by play count
            songPlayCounts.entries
                .sortedByDescending { (it.value["count"] as Int) }
                .take(limit)
                .mapNotNull { (songId, data) ->
                    val songSnapshot = songsSnapshot.child(songId)
                    TopSong(
                        songId = songId,
                        title = data["title"] as? String ?: "",
                        artist = data["artist"] as? String ?: "",
                        album = songSnapshot.child("album").value as? String ?: "",
                        coverUrl = songSnapshot.child("coverUrl").value as? String ?: "",
                        playCount = data["count"] as? Int ?: 0
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTopArtists(userId: String, limit: Int): List<TopArtist> {
        return try {
            // Get listening history
            val historySnapshot = usersRef.child(userId).child("listeningHistory")
                .orderByChild("timestamp")
                .limitToLast(1000)
                .get()
                .await()

            // Count plays per artist
            val artistPlayCounts = mutableMapOf<String, MutableMap<String, Any>>()

            historySnapshot.children.forEach { entry ->
                val artist = entry.child("artist").value as? String ?: return@forEach
                val songId = entry.child("songId").value as? String ?: ""

                if (artistPlayCounts.containsKey(artist)) {
                    val artistData = artistPlayCounts[artist]!!
                    artistData["playCount"] = (artistData["playCount"] as Int) + 1
                    @Suppress("UNCHECKED_CAST")
                    val songs = artistData["songs"] as MutableSet<String>
                    songs.add(songId)
                } else {
                    artistPlayCounts[artist] = mutableMapOf(
                        "playCount" to 1,
                        "songs" to mutableSetOf(songId)
                    )
                }
            }

            // Convert to TopArtist list and sort by play count
            artistPlayCounts.entries
                .sortedByDescending { (it.value["playCount"] as Int) }
                .take(limit)
                .map { (artistName, data) ->
                    @Suppress("UNCHECKED_CAST")
                    val songs = data["songs"] as Set<String>
                    TopArtist(
                        artistName = artistName,
                        playCount = data["playCount"] as? Int ?: 0,
                        songCount = songs.size
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getAchievements(userId: String): List<Achievement> {
        return try {
            val stats = getUserStats(userId) ?: UserStats()

            // Define achievements with progress calculation
            listOf(
                Achievement(
                    id = 1,
                    title = "Music Explorer",
                    description = "Play 100+ songs",
                    iconUrl = "",
                    isCompleted = stats.songsPlayed >= 100,
                    progress = stats.songsPlayed,
                    target = 100
                ),
                Achievement(
                    id = 2,
                    title = "Night Owl",
                    description = "Listen for 10+ hours",
                    iconUrl = "",
                    isCompleted = (stats.totalListeningTime / 3600000) >= 10,
                    progress = (stats.totalListeningTime / 3600000).toInt(),
                    target = 10
                ),
                Achievement(
                    id = 3,
                    title = "Dedicated Listener",
                    description = "Maintain a 7-day streak",
                    iconUrl = "",
                    isCompleted = stats.dayStreak >= 7,
                    progress = stats.dayStreak,
                    target = 7
                ),
                Achievement(
                    id = 4,
                    title = "Collection Builder",
                    description = "Save 20+ songs",
                    iconUrl = "",
                    isCompleted = stats.favoritesCount >= 20,
                    progress = stats.favoritesCount,
                    target = 20
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getWeeklyPattern(userId: String): List<WeeklyPattern> {
        return try {
            val calendar = Calendar.getInstance()
            val today = calendar.timeInMillis
            val oneWeekAgo = today - (7 * 24 * 60 * 60 * 1000)

            // Get listening history for the last 7 days
            val historySnapshot = usersRef.child(userId).child("listeningHistory")
                .orderByChild("timestamp")
                .startAt(oneWeekAgo.toDouble())
                .get()
                .await()

            // Group by day of week
            val weeklyData = mutableMapOf<Int, MutableMap<String, Any>>()
            for (i in 0..6) {
                weeklyData[i] = mutableMapOf(
                    "listeningTime" to 0L,
                    "songsPlayed" to 0
                )
            }

            historySnapshot.children.forEach { entry ->
                val timestamp = entry.child("timestamp").value as? Long ?: return@forEach
                val durationPlayed = entry.child("durationPlayed").value as? Long ?: 0

                calendar.timeInMillis = timestamp
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

                val dayData = weeklyData[dayOfWeek]!!
                dayData["listeningTime"] = (dayData["listeningTime"] as Long) + (durationPlayed / 60000) // Convert to minutes
                dayData["songsPlayed"] = (dayData["songsPlayed"] as Int) + 1
            }

            // Convert to WeeklyPattern list
            weeklyData.map { (day, data) ->
                WeeklyPattern(
                    dayOfWeek = day,
                    listeningTime = data["listeningTime"] as? Long ?: 0,
                    songsPlayed = data["songsPlayed"] as? Int ?: 0
                )
            }.sortedBy { it.dayOfWeek }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun trackSongPlay(
        userId: String,
        songId: String,
        songTitle: String,
        artist: String,
        durationPlayed: Long
    ): Boolean {
        return try {
            val timestamp = System.currentTimeMillis()
            android.util.Log.d("ProfileRepo", "Tracking song play: $songTitle by $artist, duration: ${durationPlayed/1000}s")

            // Add to listening history
            val historyEntry = ListeningHistoryEntry(
                songId = songId,
                songTitle = songTitle,
                artist = artist,
                timestamp = timestamp,
                durationPlayed = durationPlayed
            )

            usersRef.child(userId).child("listeningHistory")
                .child(timestamp.toString())
                .setValue(historyEntry)
                .await()
            android.util.Log.d("ProfileRepo", "Saved to listening history at /users/$userId/listeningHistory/$timestamp")

            // Update stats
            val stats = getUserStats(userId) ?: UserStats()
            val oldListeningTime = stats.totalListeningTime
            val updatedStats = stats.copy(
                totalListeningTime = stats.totalListeningTime + durationPlayed,
                songsPlayed = stats.songsPlayed + 1,
                lastActiveDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )

            android.util.Log.d("ProfileRepo", "Updated listening time: ${oldListeningTime/60000}m -> ${updatedStats.totalListeningTime/60000}m")
            android.util.Log.d("ProfileRepo", "Total songs played: ${stats.songsPlayed} -> ${updatedStats.songsPlayed}")

            // Update day streak
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
            )

            val newDayStreak = when {
                stats.lastActiveDate == yesterday -> stats.dayStreak + 1
                stats.lastActiveDate == today -> stats.dayStreak
                else -> 1
            }

            updateUserStats(userId, updatedStats.copy(dayStreak = newDayStreak))
            android.util.Log.d("ProfileRepo", "Successfully tracked song play")
            true
        } catch (e: Exception) {
            android.util.Log.e("ProfileRepo", "Error tracking song play", e)
            e.printStackTrace()
            false
        }
    }

    override suspend fun initializeUserData(userId: String, email: String, displayName: String): Boolean {
        return try {
            // Check if user data already exists
            val existingProfile = getUserProfile(userId)
            if (existingProfile != null) {
                return true // Already initialized
            }

            // Create initial profile
            val profile = UserProfile(
                userId = userId,
                email = email,
                displayName = displayName.ifEmpty { "Music Lover" },
                memberSince = System.currentTimeMillis()
            )

            // Create initial stats
            val stats = UserStats(
                lastActiveDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                dayStreak = 1
            )

            // Save to Firebase
            updateUserProfile(userId, profile)
            updateUserStats(userId, stats)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
