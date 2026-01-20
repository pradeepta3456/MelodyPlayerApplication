package com.example.musicplayerapplication.model

data class UploadProgress(
    val fileName: String,
    val progress: Float = 0f,
    val status: UploadStatus = UploadStatus.PENDING,
    val errorMessage: String? = null
)

enum class UploadStatus {
    PENDING,
    UPLOADING,
    COMPLETED,
    FAILED
}






