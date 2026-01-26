package com.example.musicplayerapplication.repository

import android.util.Log
import com.example.musicplayerapplication.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Repository for CRUD operations on users stored in Firebase Realtime Database.
 * Uses callback-based approach for reliable Firebase operations.
 *
 * Path layout:
 * - /users/{uid} -> User
 */
interface UserRepository {
    /**
     * Creates or updates a user profile in the Realtime Database using the given Firebase user.
     * This is typically called right after authentication sign-up/sign-in completes.
     */
    fun createOrUpdateUser(
        firebaseUser: FirebaseUser,
        displayName: String? = null,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun getUser(
        userId: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun setArtist(
        userId: String,
        isArtist: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}

class UserRepositoryImpl(
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference
) : UserRepository {

    private val usersRef: DatabaseReference = db.child("users")

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }

    override fun createOrUpdateUser(
        firebaseUser: FirebaseUser,
        displayName: String?,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = firebaseUser.uid
        Log.d(TAG, "Creating/updating user for uid: $uid")

        // First, check if user already exists
        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val now = System.currentTimeMillis()
                val existingUser = snapshot.getValue(User::class.java)

                val mergedUser = if (existingUser != null) {
                    // Update basic fields while preserving existing stats/flags
                    Log.d(TAG, "User exists, updating profile for uid: $uid")
                    existingUser.copy(
                        email = firebaseUser.email ?: existingUser.email,
                        displayName = displayName ?: firebaseUser.displayName ?: existingUser.displayName
                    )
                } else {
                    // Create new user
                    Log.d(TAG, "Creating new user profile for uid: $uid")
                    User(
                        id = uid,
                        email = firebaseUser.email.orEmpty(),
                        displayName = displayName ?: firebaseUser.displayName.orEmpty(),
                        createdAt = now
                    )
                }

                // Save the user to database
                usersRef.child(uid).setValue(mergedUser)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User profile saved successfully for uid: $uid")
                            onSuccess(mergedUser)
                        } else {
                            val exception = task.exception ?: Exception("Failed to save user profile")
                            Log.e(TAG, "Failed to save user profile for uid: $uid - ${exception.message}", exception)
                            onFailure(exception)
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                val exception = error.toException()
                Log.e(TAG, "Database error while creating/updating user: ${exception.message}", exception)
                onFailure(exception)
            }
        })
    }

    override fun getUser(
        userId: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d(TAG, "Fetching user for userId: $userId")

        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    Log.d(TAG, "User found for userId: $userId")
                    onSuccess(user)
                } else {
                    Log.e(TAG, "User not found for userId: $userId")
                    onFailure(IllegalStateException("User not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                val exception = error.toException()
                Log.e(TAG, "Database error while fetching user: ${exception.message}", exception)
                onFailure(exception)
            }
        })
    }

    override fun setArtist(
        userId: String,
        isArtist: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d(TAG, "Setting artist status to $isArtist for userId: $userId")

        // First get the user
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user == null) {
                    Log.e(TAG, "User not found for userId: $userId")
                    onFailure(IllegalStateException("User not found"))
                    return
                }

                // Update the user with new artist status
                val updatedUser = user.copy(isArtist = isArtist)
                usersRef.child(userId).setValue(updatedUser)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Artist status updated successfully for userId: $userId")
                            onSuccess()
                        } else {
                            val exception = task.exception ?: Exception("Failed to update artist status")
                            Log.e(TAG, "Failed to update artist status: ${exception.message}", exception)
                            onFailure(exception)
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                val exception = error.toException()
                Log.e(TAG, "Database error while updating artist status: ${exception.message}", exception)
                onFailure(exception)
            }
        })
    }
}
