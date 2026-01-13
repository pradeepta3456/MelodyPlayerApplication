package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepo {

    // ==================== AUTHENTICATION ====================

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun signInWithGoogle(
        idToken: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun logout(
        callback: (Boolean, String) -> Unit
    )

    fun getCurrentUser(): FirebaseUser?

    /**
     * Update user email address
     */
    fun updateEmail(
        newEmail: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Update user password
     */
    fun updatePassword(
        newPassword: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Re-authenticate user (required before sensitive operations)
     */
    fun reAuthenticateUser(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Send email verification
     */
    fun sendEmailVerification(
        callback: (Boolean, String) -> Unit
    )

    /**
     * Check if email is verified
     */
    fun isEmailVerified(): Boolean

    // ==================== USER PROFILE MANAGEMENT ====================

    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun editProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun getAllUsers(
        callback: (Boolean, String, List<UserModel>) -> Unit
    )

    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Update specific user field
     */
    fun updateUserField(
        userId: String,
        fieldName: String,
        value: Any,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Search users by name or email
     */
    fun searchUsers(
        query: String,
        callback: (Boolean, String, List<UserModel>) -> Unit
    )

    // ==================== FAVORITES MANAGEMENT ====================

    fun addToFavorites(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    )

    fun removeFromFavorites(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    )

    fun getFavoriteSongs(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    )

    /**
     * Clear all favorites
     */
    fun clearAllFavorites(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Check if song is in favorites
     */
    fun isSongFavorite(
        userId: String,
        songId: String,
        callback: (Boolean, String, Boolean) -> Unit
    )

    /**
     * Add multiple songs to favorites at once
     */
    fun addMultipleFavorites(
        userId: String,
        songIds: List<String>,
        callback: (Boolean, String) -> Unit
    )

    // ==================== ARTIST FOLLOWING ====================

    fun followArtist(
        userId: String,
        artistId: String,
        callback: (Boolean, String) -> Unit
    )

    fun unfollowArtist(
        userId: String,
        artistId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Get list of followed artists
     */
    fun getFollowedArtists(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    )

    /**
     * Check if user is following an artist
     */
    fun isFollowingArtist(
        userId: String,
        artistId: String,
        callback: (Boolean, String, Boolean) -> Unit
    )

    /**
     * Unfollow all artists
     */
    fun unfollowAllArtists(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    // ==================== PLAYLIST MANAGEMENT ====================

    /**
     * Add playlist to user
     */
    fun addPlaylist(
        userId: String,
        playlistId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Remove playlist from user
     */
    fun removePlaylist(
        userId: String,
        playlistId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Get user's playlists
     */
    fun getUserPlaylists(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    )

    /**
     * Update user's playlists
     */
    fun updatePlaylists(
        userId: String,
        playlistIds: List<String>,
        callback: (Boolean, String) -> Unit
    )

    // ==================== PROFILE CUSTOMIZATION ====================

    fun updateProfileImage(
        userId: String,
        imageUrl: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Remove profile image
     */
    fun removeProfileImage(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Update contact number
     */
    fun updateContact(
        userId: String,
        contact: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Update date of birth
     */
    fun updateDateOfBirth(
        userId: String,
        dob: String,
        callback: (Boolean, String) -> Unit
    )

    // ==================== PREMIUM MEMBERSHIP ====================

    fun checkPremiumStatus(
        userId: String,
        callback: (Boolean, String, Boolean) -> Unit
    )

    /**
     * Update premium status
     */
    fun updatePremiumStatus(
        userId: String,
        isPremium: Boolean,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Get premium expiry date (if applicable)
     */
    fun getPremiumExpiryDate(
        userId: String,
        callback: (Boolean, String, Long?) -> Unit
    )

    // ==================== USER STATISTICS ====================

    /**
     * Get user listening statistics
     */
    fun getUserStats(
        userId: String,
        callback: (Boolean, String, Map<String, Any>?) -> Unit
    )

    /**
     * Update user listening time
     */
    fun updateListeningTime(
        userId: String,
        minutes: Int,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Increment song play count
     */
    fun incrementPlayCount(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    )

    // ==================== SOCIAL FEATURES ====================

    /**
     * Get user's followers
     */
    fun getFollowers(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    )

    /**
     * Get users this user is following
     */
    fun getFollowing(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    )

    /**
     * Follow another user
     */
    fun followUser(
        currentUserId: String,
        targetUserId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Unfollow another user
     */
    fun unfollowUser(
        currentUserId: String,
        targetUserId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Check if following a user
     */
    fun isFollowingUser(
        currentUserId: String,
        targetUserId: String,
        callback: (Boolean, String, Boolean) -> Unit
    )

    // ==================== RECENT ACTIVITY ====================

    /**
     * Get recently played songs
     */
    fun getRecentlyPlayed(
        userId: String,
        limit: Int = 20,
        callback: (Boolean, String, List<String>) -> Unit
    )

    /**
     * Add song to recently played
     */
    fun addToRecentlyPlayed(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Clear recently played
     */
    fun clearRecentlyPlayed(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    // ==================== USER PREFERENCES ====================

    /**
     * Update user preferences/settings
     */
    fun updatePreferences(
        userId: String,
        preferences: Map<String, Any>,
        callback: (Boolean, String) -> Unit
    )

    /**
     * Get user preferences
     */
    fun getPreferences(
        userId: String,
        callback: (Boolean, String, Map<String, Any>?) -> Unit
    )

    // ==================== OFFLINE/CACHE ====================

    /**
     * Enable offline mode
     */
    fun enableOfflineMode(userId: String)

    /**
     * Disable offline mode
     */
    fun disableOfflineMode(userId: String)

    /**
     * Sync offline data
     */
    fun syncOfflineData(
        userId: String,
        callback: (Boolean, String) -> Unit
    )
}