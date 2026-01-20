package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.UploadProgress
import com.example.musicplayerapplication.model.UploadStatus
import com.example.musicplayerapplication.repository.UploadRepoImpl
import com.example.musicplayerapplication.repository.UploadRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class UploadViewModel(
    private val repository: UploadRepository = UploadRepoImpl()
) : ViewModel() {

    var uploadProgress = mutableStateOf<UploadProgress?>(null)
    var isUploading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    /**
     * Upload music file from URI
     */
    fun uploadMusicFromDevice(uri: String, fileName: String) {
        viewModelScope.launch {
            isUploading.value = true
            uploadProgress.value = UploadProgress(
                fileName = fileName,
                status = UploadStatus.UPLOADING
            )

            try {
                // Simulate upload progress
                for (i in 1..10) {
                    delay(500)
                    uploadProgress.value = uploadProgress.value?.copy(
                        progress = i / 10f
                    )
                }

                // In real app: repository.uploadFile(uri, fileName)

                uploadProgress.value = uploadProgress.value?.copy(
                    status = UploadStatus.COMPLETED,
                    progress = 1f
                )
                isUploading.value = false
            } catch (e: Exception) {
                uploadProgress.value = uploadProgress.value?.copy(
                    status = UploadStatus.FAILED,
                    errorMessage = e.message
                )
                errorMessage.value = "Upload failed: ${e.message}"
                isUploading.value = false
            }
        }
    }

    /**
     * Upload music from URL
     */
    fun uploadMusicFromUrl(url: String) {
        viewModelScope.launch {
            isUploading.value = true
            try {
                delay(2000) // Simulate download
                // In real app: repository.downloadFromUrl(url)
                isUploading.value = false
            } catch (e: Exception) {
                errorMessage.value = "Failed to download from URL"
                isUploading.value = false
            }
        }
    }

    /**
     * Record audio
     */
    fun startRecording() {
        // Start audio recording
    }

    fun stopRecording() {
        // Stop audio recording and upload
    }

    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage.value = null
    }
}






