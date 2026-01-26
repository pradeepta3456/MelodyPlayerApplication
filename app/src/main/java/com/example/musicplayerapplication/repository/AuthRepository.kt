package com.example.musicplayerapplication.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

/**
 * Repository for Firebase Authentication operations using callback-based approach.
 * This pattern is more reliable than suspend functions for Firebase operations.
 */
interface AuthRepository {
    fun signIn(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun signUp(
        email: String,
        password: String,
        displayName: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun signOut()

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getCurrentUser(): FirebaseUser?
    fun isUserLoggedIn(): Boolean
}

class AuthRepositoryImpl : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "AuthRepositoryImpl"
    }

    override fun signIn(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d(TAG, "Attempting sign in for email: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        Log.d(TAG, "Sign in successful for user: ${user.uid}")
                        onSuccess(user)
                    } else {
                        Log.e(TAG, "Sign in succeeded but user is null")
                        onFailure(Exception("Sign in failed: User is null"))
                    }
                } else {
                    val exception = task.exception ?: Exception("Unknown sign in error")
                    Log.e(TAG, "Sign in failed: ${exception.message}", exception)
                    onFailure(exception)
                }
            }
    }

    override fun signUp(
        email: String,
        password: String,
        displayName: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d(TAG, "Attempting sign up for email: $email with display name: $displayName")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        Log.d(TAG, "Account created successfully for user: ${user.uid}")

                        // Update profile with display name
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    Log.d(TAG, "Profile updated successfully with display name: $displayName")
                                    onSuccess(user)
                                } else {
                                    val exception = profileTask.exception ?: Exception("Failed to update profile")
                                    Log.e(TAG, "Profile update failed: ${exception.message}", exception)
                                    // Still return success since auth succeeded, just log the profile update failure
                                    onSuccess(user)
                                }
                            }
                    } else {
                        Log.e(TAG, "Sign up succeeded but user is null")
                        onFailure(Exception("Sign up failed: User is null"))
                    }
                } else {
                    val exception = task.exception ?: Exception("Unknown sign up error")
                    Log.e(TAG, "Sign up failed: ${exception.message}", exception)
                    onFailure(exception)
                }
            }
    }

    override fun signOut() {
        Log.d(TAG, "User signing out")
        auth.signOut()
    }

    override fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d(TAG, "Sending password reset email to: $email")

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset email sent successfully")
                    onSuccess()
                } else {
                    val exception = task.exception ?: Exception("Failed to send reset email")
                    Log.e(TAG, "Password reset failed: ${exception.message}", exception)
                    onFailure(exception)
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
