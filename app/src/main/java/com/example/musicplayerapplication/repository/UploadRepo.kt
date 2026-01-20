package com.example.musicplayerapplication.repository

import com.example.musicplayerapplication.model.UploadProgress

interface UploadRepository {
    suspend fun uploadFile(uri: String, fileName: String): Boolean
    suspend fun downloadFromUrl(url: String): Boolean
    suspend fun recordAudio(): String
    fun getUploadHistory(): List<UploadProgress>
}






