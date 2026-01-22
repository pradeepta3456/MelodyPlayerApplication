package com.example.musicplayerapplication.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.musicplayerapplication.repository.AuthRepository
import com.example.musicplayerapplication.repository.AuthRepositoryImpl
import com.example.musicplayerapplication.repository.UserRepository
import com.example.musicplayerapplication.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl(),
    private val userRepository: UserRepository = UserRepositoryImpl()
) : ViewModel() {

    var authState = mutableStateOf<AuthState>(AuthState.Idle)
        private set

    var isLoading = mutableStateOf(false)
        private set

    companion object {
        private const val TAG = "AuthViewModel"
    }

    init {
        // Check Firebase connection
        checkFirebaseConnection()
        // Check if user is already logged in
        checkCurrentUser()
    }

    private fun checkFirebaseConnection() {
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val connected = task.result?.getValue(Boolean::class.java) ?: false
                Log.d(TAG, "Firebase connected: $connected")
            } else {
                Log.e(TAG, "Failed to check Firebase connection: ${task.exception?.message}")
            }
        }
    }

    private fun checkCurrentUser() {
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            Log.d(TAG, "User already logged in: ${currentUser.uid}")
            authState.value = AuthState.Success(currentUser)
        } else {
            Log.d(TAG, "No user currently logged in")
        }
    }

    fun signIn(email: String, password: String) {
        isLoading.value = true
        authState.value = AuthState.Loading
        Log.d(TAG, "Starting sign in process for email: $email")

        repository.signIn(
            email = email,
            password = password,
            onSuccess = { user ->
                Log.d(TAG, "Sign in successful for user: ${user.uid}")
                authState.value = AuthState.Success(user)
                isLoading.value = false
            },
            onFailure = { exception ->
                Log.e(TAG, "Sign in failed: ${exception.message}", exception)
                Log.e(TAG, "Exception type: ${exception.javaClass.simpleName}")
                authState.value = AuthState.Error(getErrorMessage(exception))
                isLoading.value = false
            }
        )
    }

    fun signUp(email: String, password: String, displayName: String) {
        isLoading.value = true
        authState.value = AuthState.Loading
        Log.d(TAG, "Starting sign up process for email: $email with display name: $displayName")

        repository.signUp(
            email = email,
            password = password,
            displayName = displayName,
            onSuccess = { user ->
                Log.d(TAG, "Sign up successful for user: ${user.uid}, creating user profile in database")

                // After successful Firebase Auth sign-up, persist the user profile to Realtime DB
                userRepository.createOrUpdateUser(
                    firebaseUser = user,
                    displayName = displayName,
                    onSuccess = { createdUser ->
                        Log.d(TAG, "User profile created successfully in database for uid: ${createdUser.id}")
                        authState.value = AuthState.Success(user)
                        isLoading.value = false
                    },
                    onFailure = { dbError ->
                        Log.e(TAG, "Failed to create user profile in database: ${dbError.message}", dbError)
                        Log.e(TAG, "Database error type: ${dbError.javaClass.simpleName}")

                        // User was created in Auth but not in database
                        // Still consider it a success but log the warning
                        Log.w(TAG, "User authenticated but profile creation failed. Proceeding with auth success.")
                        authState.value = AuthState.Success(user)
                        isLoading.value = false
                    }
                )
            },
            onFailure = { exception ->
                Log.e(TAG, "Sign up failed: ${exception.message}", exception)
                Log.e(TAG, "Exception type: ${exception.javaClass.simpleName}")
                authState.value = AuthState.Error(getErrorMessage(exception))
                isLoading.value = false
            }
        )
    }

    fun signOut() {
        Log.d(TAG, "Signing out user")
        repository.signOut()
        authState.value = AuthState.Idle
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        isLoading.value = true
        Log.d(TAG, "Requesting password reset for email: $email")

        repository.resetPassword(
            email = email,
            onSuccess = {
                Log.d(TAG, "Password reset email sent successfully")
                isLoading.value = false
                onSuccess()
            },
            onFailure = { exception ->
                Log.e(TAG, "Password reset failed: ${exception.message}", exception)
                isLoading.value = false
                onError(getErrorMessage(exception))
            }
        )
    }

    fun resetAuthState() {
        Log.d(TAG, "Resetting auth state to Idle")
        authState.value = AuthState.Idle
    }

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    private fun getErrorMessage(exception: Throwable): String {
        val message = exception.message ?: ""
        Log.d(TAG, "Processing error message: $message")

        return when {
            // Network errors
            message.contains("network", ignoreCase = true) ||
                    message.contains("Unable to resolve host", ignoreCase = true) ||
                    message.contains("failed to connect", ignoreCase = true) ||
                    message.contains("timeout", ignoreCase = true) ->
                "Network error. Please check your internet connection and try again."

            // Firebase Auth errors
            message.contains("password", ignoreCase = true) ||
                    message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
                    message.contains("INVALID_PASSWORD", ignoreCase = true) ->
                "Incorrect email or password."

            message.contains("user-not-found", ignoreCase = true) ||
                    message.contains("USER_NOT_FOUND", ignoreCase = true) ->
                "No account found with this email."

            message.contains("email-already-in-use", ignoreCase = true) ||
                    message.contains("EMAIL_EXISTS", ignoreCase = true) ->
                "This email is already registered."

            message.contains("invalid-email", ignoreCase = true) ||
                    message.contains("INVALID_EMAIL", ignoreCase = true) ->
                "Please enter a valid email address."

            message.contains("weak-password", ignoreCase = true) ||
                    message.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "Password should be at least 6 characters."

            message.contains("too-many-requests", ignoreCase = true) ||
                    message.contains("TOO_MANY_ATTEMPTS", ignoreCase = true) ->
                "Too many attempts. Please try again later."

            // Database permission errors
            message.contains("PERMISSION_DENIED", ignoreCase = true) ||
                    message.contains("Permission denied", ignoreCase = true) ->
                "Database access denied. Please contact support."

            // Firebase not configured
            message.contains("FirebaseApp", ignoreCase = true) ||
                    message.contains("not initialized", ignoreCase = true) ->
                "App configuration error. Please restart the app."

            else -> {
                Log.e(TAG, "Unhandled error: $message")
                "An error occurred: ${message.take(100)}"
            }
        }
    }
}