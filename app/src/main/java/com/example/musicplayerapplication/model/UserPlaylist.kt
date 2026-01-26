package com.example.musicplayerapplication.model

/**
 * User-created playlist stored in Firebase
 * Path: /user_playlists/{userId}/{playlistId}/
 */
data class UserPlaylist(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val coverImageUrl: String = "",
    val songIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPublic: Boolean = false
) {
    // Helper to convert to Map for Firebase
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "name" to name,
            "description" to description,
            "coverImageUrl" to coverImageUrl,
            "songIds" to songIds,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "isPublic" to isPublic
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any>): UserPlaylist {
            return UserPlaylist(
                id = id,
                userId = map["userId"] as? String ?: "",
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                coverImageUrl = map["coverImageUrl"] as? String ?: "",
                songIds = (map["songIds"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                createdAt = map["createdAt"] as? Long ?: 0L,
                updatedAt = map["updatedAt"] as? Long ?: 0L,
                isPublic = map["isPublic"] as? Boolean ?: false
            )
        }
    }
}
