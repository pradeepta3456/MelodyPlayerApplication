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


    fun getFullName(): String = "$firstName $lastName".trim()


    fun getInitials(): String {
        val firstInitial = firstName.firstOrNull()?.uppercase() ?: ""
        val lastInitial = lastName.firstOrNull()?.uppercase() ?: ""
        return "$firstInitial$lastInitial"
    }


    fun hasProfileImage(): Boolean = profileImageUrl.isNotBlank()


    fun isProfileComplete(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                contact.isNotBlank() &&
                dob.isNotBlank()
    }

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

    fun getFavoritesCount(): Int = favoriteSongs.size


    fun getFollowingCount(): Int = followingArtists.size

    fun getPlaylistsCount(): Int = playlists.size


    fun isFavorite(songId: String): Boolean = favoriteSongs.contains(songId)


    fun isFollowingArtist(artistId: String): Boolean = followingArtists.contains(artistId)

    fun hasFavorites(): Boolean = favoriteSongs.isNotEmpty()


    fun hasFollowingArtists(): Boolean = followingArtists.isNotEmpty()

    fun hasPlaylists(): Boolean = playlists.isNotEmpty()


    fun isPremium(): Boolean = premiumMember


    fun getMembershipStatus(): String {
        return if (premiumMember) "Premium Member" else "Free Member"
    }


    fun getFormattedCreatedDate(): String {
        if (createdAt == 0L) return "Unknown"

        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(createdAt))
    }


    fun getAccountAgeDays(): Long {
        if (createdAt == 0L) return 0

        val now = System.currentTimeMillis()
        val diffMillis = now - createdAt
        return java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMillis)
    }


    fun isNewAccount(): Boolean {
        return getAccountAgeDays() < 7
    }


    fun hasValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    fun hasContactInfo(): Boolean = contact.isNotBlank()

    fun getDisplayName(): String {
        val fullName = getFullName()
        return if (fullName.isNotBlank()) fullName else email.substringBefore("@")
    }


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