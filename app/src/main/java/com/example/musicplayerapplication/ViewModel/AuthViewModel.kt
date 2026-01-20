package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.repository.AuthRepository
import com.example.musicplayerapplication.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    var authState = mutableStateOf<AuthState>(AuthState.Idle)
        private set

    var isLoading = mutableStateOf(false)
        private set

    init {
        // Check if user is already logged in
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            authState.value = AuthState.Success(currentUser)
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            authState.value = AuthState.Loading

            val result = repository.signIn(email, password)

            result.onSuccess { user ->
                authState.value = AuthState.Success(user)
                isLoading.value = false
            }.onFailure { exception ->
                authState.value = AuthState.Error(
                    getErrorMessage(exception)
                )
                isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            isLoading.value = true
            authState.value = AuthState.Loading

            val result = repository.signUp(email, password, displayName)

            result.onSuccess { user ->
                authState.value = AuthState.Success(user)
                isLoading.value = false
            }.onFailure { exception ->
                authState.value = AuthState.Error(
                    getErrorMessage(exception)
                )
                isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            authState.value = AuthState.Idle
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true

            val result = repository.resetPassword(email)

            result.onSuccess {
                isLoading.value = false
                onSuccess()
            }.onFailure { exception ->
                isLoading.value = false
                onError(getErrorMessage(exception))
            }
        }
    }

    fun resetAuthState() {
        authState.value = AuthState.Idle
    }

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("network", ignoreCase = true) == true ->
                "Network error. Please check your connection."

            exception.message?.contains("password", ignoreCase = true) == true ->
                "Incorrect email or password."

            exception.message?.contains("user-not-found", ignoreCase = true) == true ->
                "No account found with this email."

            exception.message?.contains("email-already-in-use", ignoreCase = true) == true ->
                "Email is already registered."

            exception.message?.contains("invalid-email", ignoreCase = true) == true ->
                "Invalid email address."

            exception.message?.contains("weak-password", ignoreCase = true) == true ->
                "Password should be at least 6 characters."

            exception.message?.contains("too-many-requests", ignoreCase = true) == true ->
                "Too many attempts. Please try again later."

            else -> exception.message ?: "An error occurred. Please try again."
        }
    }
}
