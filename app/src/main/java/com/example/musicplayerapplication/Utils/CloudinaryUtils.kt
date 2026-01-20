package com.example.musicplayerapplication.Utils

object CloudinaryUtils {
    // Cloudinary configuration
    private const val CLOUD_NAME = "drfit5xud"
    private const val API_KEY = "649351633944394"
    private const val API_SECRET = "dOKyZ9LYkoLKpkgP1zGs0oitL_k"

    // Base URL for Cloudinary
    private const val BASE_URL = "https://res.cloudinary.com/$CLOUD_NAME"

    /**
     * Generate a Cloudinary image URL with transformations
     * @param publicId The public ID of the image in Cloudinary
     * @param width Optional width for the image
     * @param height Optional height for the image
     * @param crop Optional crop mode (e.g., "fill", "fit", "scale")
     * @param quality Optional quality (e.g., "auto", "auto:best")
     * @return The complete Cloudinary URL
     */
    fun getImageUrl(
        publicId: String,
        width: Int? = null,
        height: Int? = null,
        crop: String = "fill",
        quality: String = "auto"
    ): String {
        val transformations = buildList {
            if (width != null) add("w_$width")
            if (height != null) add("h_$height")
            add("c_$crop")
            add("q_$quality")
        }.joinToString(",")

        return "$BASE_URL/image/upload/$transformations/$publicId"
    }

    /**
     * Get thumbnail URL for album covers
     * @param publicId The public ID of the album cover
     * @param size The size of the thumbnail (default 300x300)
     */
    fun getAlbumCoverUrl(publicId: String, size: Int = 300): String {
        return getImageUrl(
            publicId = publicId,
            width = size,
            height = size,
            crop = "fill",
            quality = "auto:good"
        )
    }

    /**
     * Get artist image URL
     * @param publicId The public ID of the artist image
     * @param size The size of the image
     */
    fun getArtistImageUrl(publicId: String, size: Int = 400): String {
        return getImageUrl(
            publicId = publicId,
            width = size,
            height = size,
            crop = "fill",
            quality = "auto"
        )
    }


    fun getPlaceholderUrl(): String {
        return "$BASE_URL/image/upload/c_fill,w_300,h_300,q_auto/placeholder_album.jpg"
    }

    /**
     * Upload URL for image uploads (for future use with API)
     */
    fun getUploadUrl(): String {
        return "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    }

    /**
     * Get credentials for authenticated uploads
     */
    fun getApiKey(): String = API_KEY
    fun getApiSecret(): String = API_SECRET
    fun getCloudName(): String = CLOUD_NAME
}