package com.example.musicplayerapplication.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.model.User
import com.example.musicplayerapplication.repository.UserRepository
import com.example.musicplayerapplication.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth

sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    data class Loaded(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}

class UserViewModel(
    private val repository: UserRepository = UserRepositoryImpl(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var userState = mutableStateOf<UserState>(UserState.Idle)
        private set

    companion object {
        private const val TAG = "UserViewModel"
    }

    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot load user: User not logged in")
            userState.value = UserState.Error("User not logged in")
            return
        }

        Log.d(TAG, "Loading current user for uid: $uid")
        userState.value = UserState.Loading

        repository.getUser(
            userId = uid,
            onSuccess = { user ->
                Log.d(TAG, "User loaded successfully: ${user.displayName}")
                userState.value = UserState.Loaded(user)
            },
            onFailure = { exception ->
                Log.e(TAG, "Failed to load user: ${exception.message}", exception)
                userState.value = UserState.Error(exception.message ?: "Failed to load user")
            }
        )
    }

    fun setArtist(isArtist: Boolean) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e(TAG, "Cannot set artist status: User not logged in")
            return
        }

        Log.d(TAG, "Setting artist status to $isArtist for uid: $uid")
        userState.value = UserState.Loading

        repository.setArtist(
            userId = uid,
            isArtist = isArtist,
            onSuccess = {
                Log.d(TAG, "Artist status updated successfully, refreshing user profile")
                loadCurrentUser()
            },
            onFailure = { exception ->
                Log.e(TAG, "Failed to update artist status: ${exception.message}", exception)
                userState.value = UserState.Error(exception.message ?: "Failed to update artist status")
            }
        )
    }
}