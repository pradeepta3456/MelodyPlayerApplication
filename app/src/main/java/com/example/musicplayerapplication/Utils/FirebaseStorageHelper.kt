package com.example.musicplayerapplication.Utils

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

object FirebaseStorageHelper {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Base paths for different image types
    private const val SONGS_PATH = "songs/"
    private const val ALBUMS_PATH = "albums/"
    private const val ARTISTS_PATH = "artists/"
    private const val ICONS_PATH = "icons/"
    private const val COVERS_PATH = "covers/"

    /**
     * Get download URL for a song cover image
     * @param fileName The name of the file in Firebase Storage
     * @return Download URL as String, or default placeholder if not found
     */
    suspend fun getSongCoverUrl(fileName: String): String {
        return getDownloadUrl("$SONGS_PATH$fileName")
    }

    /**
     * Get download URL for an album cover image
     * @param fileName The name of the file in Firebase Storage
     * @return Download URL as String, or default placeholder if not found
     */
    suspend fun getAlbumCoverUrl(fileName: String): String {
        return getDownloadUrl("$ALBUMS_PATH$fileName")
    }

    /**
     * Get download URL for an artist image
     * @param fileName The name of the file in Firebase Storage
     * @return Download URL as String, or default placeholder if not found
     */
    suspend fun getArtistImageUrl(fileName: String): String {
        return getDownloadUrl("$ARTISTS_PATH$fileName")
    }

    /**
     * Get download URL for an icon (achievements, etc.)
     * @param fileName The name of the file in Firebase Storage
     * @return Download URL as String, or default placeholder if not found
     */
    suspend fun getIconUrl(fileName: String): String {
        return getDownloadUrl("$ICONS_PATH$fileName")
    }

    /**
     * Get download URL for a generic cover image
     * @param fileName The name of the file in Firebase Storage
     * @return Download URL as String, or default placeholder if not found
     */
    suspend fun getCoverUrl(fileName: String): String {
        return getDownloadUrl("$COVERS_PATH$fileName")
    }

    /**
     * Generic method to get download URL from Firebase Storage
     * @param path Full path to the file in Firebase Storage
     * @return Download URL as String, or default placeholder if not found
     */
    private suspend fun getDownloadUrl(path: String): String {
        return try {
            val storageRef: StorageReference = storage.reference.child(path)
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            // Return a default placeholder URL if image not found
            getPlaceholderUrl()
        }
    }

    /**
     * Upload an image to Firebase Storage
     * @param path Full path where the file should be stored
     * @param imageData ByteArray of the image data
     * @return Download URL of the uploaded image
     */
    suspend fun uploadImage(path: String, imageData: ByteArray): String {
        return try {
            val storageRef: StorageReference = storage.reference.child(path)
            storageRef.putBytes(imageData).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload image: ${e.message}")
        }
    }

    /**
     * Delete an image from Firebase Storage
     * @param path Full path to the file in Firebase Storage
     * @return True if successful, false otherwise
     */
    suspend fun deleteImage(path: String): Boolean {
        return try {
            val storageRef: StorageReference = storage.reference.child(path)
            storageRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get a placeholder URL for missing images
     * Uses a generic music note icon as placeholder
     */
    fun getPlaceholderUrl(): String {
        // You can host a default placeholder image in Firebase Storage
        // For now, returning an empty string which Coil can handle with a placeholder
        return ""
    }

    /**
     * Direct URL builder for when you already have the file name
     * This is useful for sample data where you know the exact Firebase Storage structure
     */
    fun buildFirebaseUrl(bucketName: String = "chillvibes-e80df.firebasestorage.app", path: String): String {
        return "https://firebasestorage.googleapis.com/v0/b/$bucketName/o/${path.replace("/", "%2F")}?alt=media"
    }
}
