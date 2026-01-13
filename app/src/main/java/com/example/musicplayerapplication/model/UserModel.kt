package com.example.musicplayerapplication.model

data class UserModel(
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val contact: String = "",
    val dob: String = "",

    // Music player specific fields
    val profileImageUrl: String = "",
    val favoriteSongs: List<String> = emptyList(),
    val playlists: List<String> = emptyList(),
    val followingArtists: List<String> = emptyList(),
    val premiumMember: Boolean = false,
    val createdAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "contact" to contact,
            "dob" to dob,
            "profileImageUrl" to profileImageUrl,
            "favoriteSongs" to favoriteSongs,
            "playlists" to playlists,
            "followingArtists" to followingArtists,
            "premiumMember" to premiumMember,
            "createdAt" to createdAt
        )
    }

    // ==================== PROFILE HELPERS ====================

    /**
     * Get user's full name
     */
    fun getFullName(): String = "$firstName $lastName".trim()

    /**
     * Get user's initials (e.g., "John Doe" -> "JD")
     */
    fun getInitials(): String {
        val firstInitial = firstName.firstOrNull()?.uppercase() ?: ""
        val lastInitial = lastName.firstOrNull()?.uppercase() ?: ""
        return "$firstInitial$lastInitial"
    }

    /**
     * Check if user has profile image
     */
    fun hasProfileImage(): Boolean = profileImageUrl.isNotBlank()

    /**
     * Check if profile is complete (all required fields filled)
     */
    fun isProfileComplete(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                contact.isNotBlank() &&
                dob.isNotBlank()
    }

    /**
     * Get profile completion percentage (0-100)
     */
    fun getProfileCompletionPercentage(): Int {
        var completed = 0
        val totalFields = 6

        if (firstName.isNotBlank()) completed++
        if (lastName.isNotBlank()) completed++
        if (email.isNotBlank()) completed++
        if (contact.isNotBlank()) completed++
        if (dob.isNotBlank()) completed++
        if (profileImageUrl.isNotBlank()) completed++

        return (completed * 100) / totalFields
    }

    // ==================== MUSIC FEATURES HELPERS ====================

    /**
     * Get favorite songs count
     */
    fun getFavoritesCount(): Int = favoriteSongs.size

    /**
     * Get following artists count
     */
    fun getFollowingCount(): Int = followingArtists.size

    /**
     * Get playlists count
     */
    fun getPlaylistsCount(): Int = playlists.size

    /**
     * Check if a song is in favorites
     */
    fun isFavorite(songId: String): Boolean = favoriteSongs.contains(songId)

    /**
     * Check if following an artist
     */
    fun isFollowingArtist(artistId: String): Boolean = followingArtists.contains(artistId)

    /**
     * Check if user has any favorites
     */
    fun hasFavorites(): Boolean = favoriteSongs.isNotEmpty()

    /**
     * Check if user follows any artists
     */
    fun hasFollowingArtists(): Boolean = followingArtists.isNotEmpty()

    /**
     * Check if user has any playlists
     */
    fun hasPlaylists(): Boolean = playlists.isNotEmpty()

    // ==================== PREMIUM FEATURES ====================

    /**
     * Check if user is premium member
     */
    fun isPremium(): Boolean = premiumMember

    /**
     * Get membership status text
     */
    fun getMembershipStatus(): String {
        return if (premiumMember) "Premium Member" else "Free Member"
    }

    // ==================== DATE/TIME HELPERS ====================

    /**
     * Get formatted creation date
     */
    fun getFormattedCreatedDate(): String {
        if (createdAt == 0L) return "Unknown"

        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(createdAt))
    }

    /**
     * Get account age in days
     */
    fun getAccountAgeDays(): Long {
        if (createdAt == 0L) return 0

        val now = System.currentTimeMillis()
        val diffMillis = now - createdAt
        return java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMillis)
    }

    /**
     * Check if account is new (less than 7 days old)
     */
    fun isNewAccount(): Boolean {
        return getAccountAgeDays() < 7
    }

    // ==================== VALIDATION HELPERS ====================

    /**
     * Validate email format
     */
    fun hasValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Check if user has contact info
     */
    fun hasContactInfo(): Boolean = contact.isNotBlank()

    // ==================== DISPLAY HELPERS ====================

    /**
     * Get display name (full name or email if name not available)
     */
    fun getDisplayName(): String {
        val fullName = getFullName()
        return if (fullName.isNotBlank()) fullName else email.substringBefore("@")
    }

    /**
     * Get short description for profile
     */
    fun getProfileDescription(): String {
        val favorites = getFavoritesCount()
        val following = getFollowingCount()
        val playlists = getPlaylistsCount()

        return buildString {
            if (favorites > 0) append("$favorites songs • ")
            if (following > 0) append("$following artists • ")
            if (playlists > 0) append("$playlists playlists")
            if (isEmpty()) append("No activity yet")
        }.trim().removeSuffix("•").trim()
    }
}