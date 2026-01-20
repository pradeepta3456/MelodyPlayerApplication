package com.example.musicplayerapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.model.UserModel
import com.example.musicplayerapplication.repository.UserRepo
import com.google.firebase.auth.FirebaseUser

class UserViewModel(private val repo: UserRepo) : ViewModel() {

    // ==================== LiveData ====================

    private val _currentUser = MutableLiveData<UserModel?>()
    val currentUser: LiveData<UserModel?> get() = _currentUser

    private val _allUsers = MutableLiveData<List<UserModel>>()
    val allUsers: LiveData<List<UserModel>> get() = _allUsers

    private val _favoriteSongs = MutableLiveData<List<String>>()
    val favoriteSongs: LiveData<List<String>> get() = _favoriteSongs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // ==================== AUTHENTICATION ====================

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.login(email, password) { success, message ->
            _isLoading.postValue(false)
            if (!success) {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.register(email, password) { success, message, userId ->
            _isLoading.postValue(false)
            if (!success) {
                _error.postValue(message)
            }
            callback(success, message, userId)
        }
    }

    fun signInWithGoogle(
        idToken: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.signInWithGoogle(idToken) { success, message, userId ->
            _isLoading.postValue(false)
            if (!success) {
                _error.postValue(message)
            }
            callback(success, message, userId)
        }
    }

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.forgetPassword(email) { success, message ->
            _isLoading.postValue(false)
            if (!success) {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout { success, message ->
            if (success) {
                _currentUser.postValue(null)
                _favoriteSongs.postValue(emptyList())
            }
            callback(success, message)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    // ==================== USER PROFILE ====================

    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.addUserToDatabase(userId, model) { success, message ->
            _isLoading.postValue(false)
            if (success) {
                _currentUser.postValue(model)
            } else {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    fun editProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.editProfile(userId, model) { success, message ->
            _isLoading.postValue(false)
            if (success) {
                _currentUser.postValue(model)
            } else {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    fun getUserById(userId: String) {
        _isLoading.postValue(true)
        repo.getUserById(userId) { success, message, data ->
            _isLoading.postValue(false)
            if (success && data != null) {
                _currentUser.postValue(data)
            } else {
                _currentUser.postValue(null)
                _error.postValue(message)
            }
        }
    }

    fun getAllUsers() {
        _isLoading.postValue(true)
        repo.getAllUsers { success, message, data ->
            _isLoading.postValue(false)
            if (success) {
                _allUsers.postValue(data)
            } else {
                _allUsers.postValue(emptyList())
                _error.postValue(message)
            }
        }
    }

    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.deleteAccount(userId) { success, message ->
            _isLoading.postValue(false)
            if (success) {
                _currentUser.postValue(null)
                _favoriteSongs.postValue(emptyList())
            } else {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    // ==================== FAVORITES ====================

    fun addToFavorites(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addToFavorites(userId, songId) { success, message ->
            if (success) {
                // Refresh favorites list
                getFavoriteSongs(userId)
            } else {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    fun removeFromFavorites(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.removeFromFavorites(userId, songId) { success, message ->
            if (success) {
                // Refresh favorites list
                getFavoriteSongs(userId)
            } else {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    fun getFavoriteSongs(userId: String) {
        repo.getFavoriteSongs(userId) { success, message, songs ->
            if (success) {
                _favoriteSongs.postValue(songs)
            } else {
                _favoriteSongs.postValue(emptyList())
                _error.postValue(message)
            }
        }
    }

    // ==================== ARTISTS ====================

    fun followArtist(
        userId: String,
        artistId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.followArtist(userId, artistId) { success, message ->
            if (success) {
                // Refresh user profile to update following list
                getUserById(userId)
            } else {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    fun unfollowArtist(
        userId: String,
        artistId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.unfollowArtist(userId, artistId) { success, message ->
            if (success) {
                // Refresh user profile to update following list
                getUserById(userId)
            } else {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    // ==================== PROFILE IMAGE ====================

    fun updateProfileImage(
        userId: String,
        imageUrl: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateProfileImage(userId, imageUrl) { success, message ->
            if (success) {
                // Refresh user profile to show new image
                getUserById(userId)
            } else {
                _error.postValue(message)
            }
            callback(success, message)
        }
    }

    // ==================== PREMIUM STATUS ====================

    fun checkPremiumStatus(
        userId: String,
        callback: (Boolean, String, Boolean) -> Unit
    ) {
        repo.checkPremiumStatus(userId) { success, message, isPremium ->
            if (!success) {
                _error.postValue(message)
            }
            callback(success, message, isPremium)
        }
    }

    // ==================== HELPER METHODS ====================

    fun clearError() {
        _error.postValue(null)
    }

    fun isUserLoggedIn(): Boolean {
        return getCurrentUser() != null
    }

    fun getCurrentUserId(): String? {
        return getCurrentUser()?.uid
    }
}