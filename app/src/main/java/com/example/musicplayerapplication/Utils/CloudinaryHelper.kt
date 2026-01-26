package com.example.musicplayerapplication.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Cloudinary Helper for uploading audio and image files
 *
 * Setup Instructions:
 * 1. Sign up for free at https://cloudinary.com/users/register_free
 * 2. Get your Cloud Name, API Key, and API Secret from Dashboard
 * 3. Initialize in Application class or MainActivity
 */
object CloudinaryHelper {

    private const val TAG = "CloudinaryHelper"
    private var isInitialized = false

    /**
     * Initialize Cloudinary with your credentials
     * Call this in your Application onCreate() or MainActivity
     *
     * @param context Application context
     * @param cloudName Your Cloudinary cloud name
     * @param apiKey Your Cloudinary API key
     * @param apiSecret Your Cloudinary API secret
     */
    fun initialize(context: Context, cloudName: String, apiKey: String, apiSecret: String) {
        if (isInitialized) {
            Log.d(TAG, "Cloudinary already initialized")
            return
        }

        try {
            val config = mapOf(
                "cloud_name" to cloudName,
                "api_key" to apiKey,
                "api_secret" to apiSecret,
                "secure" to true
            )
            MediaManager.init(context, config)
            isInitialized = true
            Log.d(TAG, "Cloudinary initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Cloudinary", e)
            throw e
        }
    }

    /**
     * Check if Cloudinary is initialized
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * Upload audio file to Cloudinary
     *
     * @param context Application context
     * @param audioUri URI of the audio file
     * @param fileName Name for the uploaded file (without extension)
     * @param onProgress Callback for upload progress (0.0 to 1.0)
     * @return Cloudinary secure URL of the uploaded audio
     */
    suspend fun uploadAudio(
        context: Context,
        audioUri: Uri,
        fileName: String,
        onProgress: (Float) -> Unit
    ): String = suspendCancellableCoroutine { continuation ->

        if (!isInitialized) {
            continuation.resumeWithException(
                IllegalStateException("Cloudinary not initialized. Call initialize() first.")
            )
            return@suspendCancellableCoroutine
        }

        try {
            val requestId = MediaManager.get()
                .upload(audioUri)
                .option("resource_type", "video") // Cloudinary uses 'video' for audio files
                .option("folder", "music_app/audio")
                .option("public_id", fileName)
                .option("overwrite", false)
                .option("use_filename", true)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Upload started: $requestId")
                        onProgress(0f)
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = bytes.toFloat() / totalBytes.toFloat()
                        onProgress(progress)
                        Log.d(TAG, "Upload progress: ${(progress * 100).toInt()}%")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        if (secureUrl != null) {
                            Log.d(TAG, "Upload successful: $secureUrl")
                            continuation.resume(secureUrl)
                        } else {
                            continuation.resumeWithException(
                                Exception("Upload succeeded but no URL returned")
                            )
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Upload error: ${error.description}")
                        continuation.resumeWithException(
                            Exception("Upload failed: ${error.description}")
                        )
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.w(TAG, "Upload rescheduled: ${error.description}")
                    }
                })
                .dispatch()

            continuation.invokeOnCancellation {
                Log.d(TAG, "Upload cancelled: $requestId")
                // Cloudinary doesn't provide a direct cancel method, but this handles cleanup
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error starting upload", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Upload image file to Cloudinary
     *
     * @param context Application context
     * @param imageUri URI of the image file
     * @param fileName Name for the uploaded file (without extension)
     * @param onProgress Callback for upload progress (0.0 to 1.0)
     * @return Cloudinary secure URL of the uploaded image
     */
    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        fileName: String,
        onProgress: (Float) -> Unit
    ): String = suspendCancellableCoroutine { continuation ->

        if (!isInitialized) {
            continuation.resumeWithException(
                IllegalStateException("Cloudinary not initialized. Call initialize() first.")
            )
            return@suspendCancellableCoroutine
        }

        try {
            val requestId = MediaManager.get()
                .upload(imageUri)
                .option("resource_type", "image")
                .option("folder", "music_app/covers")
                .option("public_id", fileName)
                .option("overwrite", false)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Image upload started: $requestId")
                        onProgress(0f)
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = bytes.toFloat() / totalBytes.toFloat()
                        onProgress(progress)
                        Log.d(TAG, "Image upload progress: ${(progress * 100).toInt()}%")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        if (secureUrl != null) {
                            Log.d(TAG, "Image upload successful: $secureUrl")
                            continuation.resume(secureUrl)
                        } else {
                            continuation.resumeWithException(
                                Exception("Image upload succeeded but no URL returned")
                            )
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Image upload error: ${error.description}")
                        continuation.resumeWithException(
                            Exception("Image upload failed: ${error.description}")
                        )
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.w(TAG, "Image upload rescheduled: ${error.description}")
                    }
                })
                .dispatch()

            continuation.invokeOnCancellation {
                Log.d(TAG, "Image upload cancelled: $requestId")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error starting image upload", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Upload cover image with optimized settings for thumbnails
     *
     * @param context Application context
     * @param imageUri URI of the image file
     * @param fileName Name for the uploaded file
     * @return Cloudinary secure URL of the uploaded thumbnail
     */
    suspend fun uploadCoverThumbnail(
        context: Context,
        imageUri: Uri,
        fileName: String
    ): String = suspendCancellableCoroutine { continuation ->

        if (!isInitialized) {
            continuation.resumeWithException(
                IllegalStateException("Cloudinary not initialized. Call initialize() first.")
            )
            return@suspendCancellableCoroutine
        }

        try {
            MediaManager.get()
                .upload(imageUri)
                .option("resource_type", "image")
                .option("folder", "music_app/thumbnails")
                .option("public_id", fileName)
                .option("overwrite", false)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Thumbnail upload started")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // Thumbnail uploads are usually fast, no need to track progress
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        if (secureUrl != null) {
                            Log.d(TAG, "Thumbnail upload successful")
                            continuation.resume(secureUrl)
                        } else {
                            continuation.resumeWithException(
                                Exception("Thumbnail upload succeeded but no URL returned")
                            )
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Thumbnail upload error: ${error.description}")
                        continuation.resumeWithException(
                            Exception("Thumbnail upload failed: ${error.description}")
                        )
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.w(TAG, "Thumbnail upload rescheduled")
                    }
                })
                .dispatch()

        } catch (e: Exception) {
            Log.e(TAG, "Error starting thumbnail upload", e)
            continuation.resumeWithException(e)
        }
    }
}
