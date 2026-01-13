package com.example.musicplayerapplication.repository

import android.util.Log
import com.example.musicplayerapplication.model.UserModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {

    // Firebase instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("Users")

    companion object {
        private const val TAG = "UserRepoImpl"
    }

    init {
        try {
            database.setPersistenceEnabled(true)
            Log.d(TAG, "Firebase persistence enabled")
        } catch (e: Exception) {
            Log.d(TAG, "Firebase persistence already enabled")
        }
        Log.d(TAG, "Database reference: ${usersRef.toString()}")
    }

    // ==================== AUTHENTICATION ====================

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        Log.d(TAG, "Login attempt for email: $email")

        if (email.isBlank() || password.isBlank()) {
            Log.w(TAG, "Login failed: Empty credentials")
            callback(false, "Email and password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Login successful for: $email")
                    callback(true, "Login successful")
                } else {
                    Log.e(TAG, "Login failed: ${task.exception?.message}")
                    callback(false, task.exception?.message ?: "Login failed")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        Log.d(TAG, "Registration attempt for email: $email")

        if (email.isBlank() || password.isBlank()) {
            Log.w(TAG, "Registration failed: Empty credentials")
            callback(false, "Email and password cannot be empty", "")
            return
        }

        if (password.length < 6) {
            Log.w(TAG, "Registration failed: Password too short")
            callback(false, "Password must be at least 6 characters", "")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    Log.d(TAG, "Registration successful. UserId: $userId")
                    callback(true, "Registration successful", userId)
                } else {
                    Log.e(TAG, "Registration failed: ${task.exception?.message}")
                    callback(false, task.exception?.message ?: "Registration failed", "")
                }
            }
    }

    override fun signInWithGoogle(
        idToken: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        Log.d(TAG, "Google sign-in attempt")

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid ?: ""

                    Log.d(TAG, "Google auth successful. UserId: $userId")

                    if (userId.isEmpty()) {
                        Log.e(TAG, "UserId is empty")
                        callback(false, "Failed to get user ID", "")
                        return@addOnCompleteListener
                    }

                    usersRef.child(userId).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists()) {
                                    val displayName = user?.displayName ?: ""
                                    val nameParts = displayName.split(" ", limit = 2)

                                    val newUser = UserModel(
                                        userId = userId,
                                        email = user?.email ?: "",
                                        firstName = nameParts.getOrNull(0) ?: "",
                                        lastName = nameParts.getOrNull(1) ?: "",
                                        profileImageUrl = user?.photoUrl?.toString() ?: "",
                                        createdAt = System.currentTimeMillis()
                                    )

                                    Log.d(TAG, "Creating new user: ${newUser.email}")

                                    usersRef.child(userId).setValue(newUser)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "✅ User saved successfully")
                                            callback(true, "Google sign-in successful", userId)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "❌ Failed to save: ${e.message}")
                                            callback(false, "Failed: ${e.message}", "")
                                        }
                                } else {
                                    Log.d(TAG, "User exists")
                                    callback(true, "Welcome back!", userId)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Database error: ${error.message}")
                                callback(false, error.message, "")
                            }
                        })
                } else {
                    Log.e(TAG, "Google auth failed: ${task.exception?.message}")
                    callback(false, task.exception?.message ?: "Google sign-in failed", "")
                }
            }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (email.isBlank()) {
            callback(false, "Email cannot be empty")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password reset link sent to $email")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logged out successfully")
        } catch (e: Exception) {
            callback(false, e.message ?: "Logout failed")
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun updateEmail(
        newEmail: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            callback(false, "No user logged in")
            return
        }

        user.updateEmail(newEmail)
            .addOnSuccessListener {
                callback(true, "Email updated successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update email")
            }
    }

    override fun updatePassword(
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            callback(false, "No user logged in")
            return
        }

        if (newPassword.length < 6) {
            callback(false, "Password must be at least 6 characters")
            return
        }

        user.updatePassword(newPassword)
            .addOnSuccessListener {
                callback(true, "Password updated successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update password")
            }
    }

    override fun reAuthenticateUser(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            callback(false, "No user logged in")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                callback(true, "Re-authentication successful")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Re-authentication failed")
            }
    }

    override fun sendEmailVerification(callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            callback(false, "No user logged in")
            return
        }

        user.sendEmailVerification()
            .addOnSuccessListener {
                callback(true, "Verification email sent")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to send verification email")
            }
    }

    override fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }

    // ==================== USER PROFILE ====================

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).setValue(model)
            .addOnSuccessListener {
                callback(true, "User data saved successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to save user data")
            }
    }

    override fun editProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).updateChildren(model.toMap())
            .addOnSuccessListener {
                callback(true, "Profile updated successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update profile")
            }
    }

    override fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        callback(true, "Profile fetched successfully", user)
                    } else {
                        callback(false, "Failed to parse user data", null)
                    }
                } else {
                    callback(false, "User not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val allUsers = mutableListOf<UserModel>()
                    for (data in snapshot.children) {
                        val user = data.getValue(UserModel::class.java)
                        if (user != null) {
                            allUsers.add(user)
                        }
                    }
                    callback(true, "Users fetched successfully", allUsers)
                } else {
                    callback(false, "No users found", emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).removeValue().addOnCompleteListener { dbTask ->
            if (dbTask.isSuccessful) {
                auth.currentUser?.delete()?.addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        callback(true, "Account deleted successfully")
                    } else {
                        callback(false, authTask.exception?.message ?: "Failed to delete account")
                    }
                } ?: callback(false, "No user currently signed in")
            } else {
                callback(false, dbTask.exception?.message ?: "Failed to delete user data")
            }
        }
    }

    override fun updateUserField(
        userId: String,
        fieldName: String,
        value: Any,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child(fieldName).setValue(value)
            .addOnSuccessListener {
                callback(true, "Field updated successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update field")
            }
    }

    override fun searchUsers(
        query: String,
        callback: (Boolean, String, List<UserModel>) -> Unit
    ) {
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val results = mutableListOf<UserModel>()
                val lowerQuery = query.lowercase()

                for (data in snapshot.children) {
                    val user = data.getValue(UserModel::class.java)
                    if (user != null) {
                        val fullName = "${user.firstName} ${user.lastName}".lowercase()
                        val email = user.email.lowercase()

                        if (fullName.contains(lowerQuery) || email.contains(lowerQuery)) {
                            results.add(user)
                        }
                    }
                }
                callback(true, "Search completed", results)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    // ==================== FAVORITES ====================

    override fun addToFavorites(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("favoriteSongs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentFavorites = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                currentFavorites.add(it)
                            }
                        }
                    }

                    if (currentFavorites.contains(songId)) {
                        callback(false, "Song already in favorites")
                        return
                    }

                    currentFavorites.add(songId)

                    usersRef.child(userId).child("favoriteSongs").setValue(currentFavorites)
                        .addOnSuccessListener {
                            callback(true, "Added to favorites")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to add to favorites")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun removeFromFavorites(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("favoriteSongs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentFavorites = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                currentFavorites.add(it)
                            }
                        }
                    }

                    if (currentFavorites.remove(songId)) {
                        usersRef.child(userId).child("favoriteSongs").setValue(currentFavorites)
                            .addOnSuccessListener {
                                callback(true, "Removed from favorites")
                            }
                            .addOnFailureListener { e ->
                                callback(false, e.message ?: "Failed to remove from favorites")
                            }
                    } else {
                        callback(false, "Song not found in favorites")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun getFavoriteSongs(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    ) {
        usersRef.child(userId).child("favoriteSongs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorites = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                favorites.add(it)
                            }
                        }
                        callback(true, "Favorites fetched successfully", favorites)
                    } else {
                        callback(true, "No favorites found", emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun clearAllFavorites(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("favoriteSongs").setValue(emptyList<String>())
            .addOnSuccessListener {
                callback(true, "All favorites cleared")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to clear favorites")
            }
    }

    override fun isSongFavorite(
        userId: String,
        songId: String,
        callback: (Boolean, String, Boolean) -> Unit
    ) {
        usersRef.child(userId).child("favoriteSongs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorites = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                favorites.add(it)
                            }
                        }
                    }

                    callback(true, "Check completed", favorites.contains(songId))
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, false)
                }
            })
    }

    override fun addMultipleFavorites(
        userId: String,
        songIds: List<String>,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("favoriteSongs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentFavorites = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                currentFavorites.add(it)
                            }
                        }
                    }

                    // Add only unique songs
                    songIds.forEach { songId ->
                        if (!currentFavorites.contains(songId)) {
                            currentFavorites.add(songId)
                        }
                    }

                    usersRef.child(userId).child("favoriteSongs").setValue(currentFavorites)
                        .addOnSuccessListener {
                            callback(true, "Songs added to favorites")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to add songs")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    // ==================== ARTISTS ====================

    override fun followArtist(
        userId: String,
        artistId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("followingArtists")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentFollowing = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                currentFollowing.add(it)
                            }
                        }
                    }

                    if (currentFollowing.contains(artistId)) {
                        callback(false, "Already following this artist")
                        return
                    }

                    currentFollowing.add(artistId)

                    usersRef.child(userId).child("followingArtists").setValue(currentFollowing)
                        .addOnSuccessListener {
                            callback(true, "Now following artist")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to follow artist")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun unfollowArtist(
        userId: String,
        artistId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("followingArtists")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentFollowing = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                currentFollowing.add(it)
                            }
                        }
                    }

                    if (currentFollowing.remove(artistId)) {
                        usersRef.child(userId).child("followingArtists").setValue(currentFollowing)
                            .addOnSuccessListener {
                                callback(true, "Unfollowed artist")
                            }
                            .addOnFailureListener { e ->
                                callback(false, e.message ?: "Failed to unfollow artist")
                            }
                    } else {
                        callback(false, "Not following this artist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun getFollowedArtists(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    ) {
        usersRef.child(userId).child("followingArtists")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val artists = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                artists.add(it)
                            }
                        }
                    }
                    callback(true, "Artists fetched", artists)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun isFollowingArtist(
        userId: String,
        artistId: String,
        callback: (Boolean, String, Boolean) -> Unit
    ) {
        usersRef.child(userId).child("followingArtists")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val artists = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                artists.add(it)
                            }
                        }
                    }

                    callback(true, "Check completed", artists.contains(artistId))
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, false)
                }
            })
    }

    override fun unfollowAllArtists(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("followingArtists").setValue(emptyList<String>())
            .addOnSuccessListener {
                callback(true, "Unfollowed all artists")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to unfollow all")
            }
    }

    // ==================== PLAYLIST MANAGEMENT ====================

    override fun addPlaylist(
        userId: String,
        playlistId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("playlists")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playlists = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                playlists.add(it)
                            }
                        }
                    }

                    if (!playlists.contains(playlistId)) {
                        playlists.add(playlistId)
                    }

                    usersRef.child(userId).child("playlists").setValue(playlists)
                        .addOnSuccessListener {
                            callback(true, "Playlist added")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to add playlist")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun removePlaylist(
        userId: String,
        playlistId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("playlists")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playlists = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                playlists.add(it)
                            }
                        }
                    }

                    playlists.remove(playlistId)

                    usersRef.child(userId).child("playlists").setValue(playlists)
                        .addOnSuccessListener {
                            callback(true, "Playlist removed")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to remove playlist")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun getUserPlaylists(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    ) {
        usersRef.child(userId).child("playlists")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playlists = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let {
                                playlists.add(it)
                            }
                        }
                    }
                    callback(true, "Playlists fetched", playlists)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun updatePlaylists(
        userId: String,
        playlistIds: List<String>,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("playlists").setValue(playlistIds)
            .addOnSuccessListener {
                callback(true, "Playlists updated")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update playlists")
            }
    }

    // ==================== PROFILE CUSTOMIZATION ====================

    override fun updateProfileImage(
        userId: String,
        imageUrl: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (imageUrl.isBlank()) {
            callback(false, "Image URL cannot be empty")
            return
        }

        usersRef.child(userId).child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener {
                callback(true, "Profile image updated")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update image")
            }
    }

    override fun removeProfileImage(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("profileImageUrl").setValue("")
            .addOnSuccessListener {
                callback(true, "Profile image removed")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to remove image")
            }
    }

    override fun updateContact(
        userId: String,
        contact: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("contact").setValue(contact)
            .addOnSuccessListener {
                callback(true, "Contact updated")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update contact")
            }
    }

    override fun updateDateOfBirth(
        userId: String,
        dob: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("dob").setValue(dob)
            .addOnSuccessListener {
                callback(true, "Date of birth updated")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update DOB")
            }
    }

    // ==================== PREMIUM MEMBERSHIP ====================

    override fun checkPremiumStatus(
        userId: String,
        callback: (Boolean, String, Boolean) -> Unit
    ) {
        usersRef.child(userId).child("premiumMember")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isPremium = snapshot.getValue(Boolean::class.java) ?: false
                    callback(true, "Premium status fetched", isPremium)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, false)
                }
            })
    }

    override fun updatePremiumStatus(
        userId: String,
        isPremium: Boolean,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("premiumMember").setValue(isPremium)
            .addOnSuccessListener {
                callback(true, "Premium status updated")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update premium status")
            }
    }

    override fun getPremiumExpiryDate(
        userId: String,
        callback: (Boolean, String, Long?) -> Unit
    ) {
        usersRef.child(userId).child("premiumExpiryDate")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expiryDate = snapshot.getValue(Long::class.java)
                    callback(true, "Expiry date fetched", expiryDate)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    // ==================== USER STATISTICS ====================

    override fun getUserStats(
        userId: String,
        callback: (Boolean, String, Map<String, Any>?) -> Unit
    ) {
        usersRef.child(userId).child("stats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stats = snapshot.value as? Map<String, Any>
                    callback(true, "Stats fetched", stats)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun updateListeningTime(
        userId: String,
        minutes: Int,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("stats").child("listeningTime")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentMinutes = snapshot.getValue(Int::class.java) ?: 0
                    val newTotal = currentMinutes + minutes

                    usersRef.child(userId).child("stats").child("listeningTime").setValue(newTotal)
                        .addOnSuccessListener {
                            callback(true, "Listening time updated")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to update")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun incrementPlayCount(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    ) {
        val path = usersRef.child(userId).child("stats").child("playCounts").child(songId)

        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentCount = snapshot.getValue(Int::class.java) ?: 0
                path.setValue(currentCount + 1)
                    .addOnSuccessListener {
                        callback(true, "Play count incremented")
                    }
                    .addOnFailureListener { e ->
                        callback(false, e.message ?: "Failed to increment")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message)
            }
        })
    }

    // ==================== SOCIAL FEATURES ====================

    override fun getFollowers(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    ) {
        usersRef.child(userId).child("followers")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val followers = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { followers.add(it) }
                        }
                    }
                    callback(true, "Followers fetched", followers)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getFollowing(
        userId: String,
        callback: (Boolean, String, List<String>) -> Unit
    ) {
        usersRef.child(userId).child("following")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val following = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { following.add(it) }
                        }
                    }
                    callback(true, "Following fetched", following)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun followUser(
        currentUserId: String,
        targetUserId: String,
        callback: (Boolean, String) -> Unit
    ) {
        // Add to current user's following list
        usersRef.child(currentUserId).child("following")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val following = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { following.add(it) }
                        }
                    }

                    if (!following.contains(targetUserId)) {
                        following.add(targetUserId)
                    }

                    usersRef.child(currentUserId).child("following").setValue(following)
                        .addOnSuccessListener {
                            // Add to target user's followers list
                            addToFollowersList(targetUserId, currentUserId, callback)
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to follow user")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    private fun addToFollowersList(
        userId: String,
        followerId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("followers")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val followers = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { followers.add(it) }
                        }
                    }

                    if (!followers.contains(followerId)) {
                        followers.add(followerId)
                    }

                    usersRef.child(userId).child("followers").setValue(followers)
                        .addOnSuccessListener {
                            callback(true, "Now following user")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to update followers")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun unfollowUser(
        currentUserId: String,
        targetUserId: String,
        callback: (Boolean, String) -> Unit
    ) {
        // Remove from current user's following list
        usersRef.child(currentUserId).child("following")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val following = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { following.add(it) }
                        }
                    }

                    following.remove(targetUserId)

                    usersRef.child(currentUserId).child("following").setValue(following)
                        .addOnSuccessListener {
                            // Remove from target user's followers list
                            removeFromFollowersList(targetUserId, currentUserId, callback)
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to unfollow user")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    private fun removeFromFollowersList(
        userId: String,
        followerId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("followers")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val followers = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { followers.add(it) }
                        }
                    }

                    followers.remove(followerId)

                    usersRef.child(userId).child("followers").setValue(followers)
                        .addOnSuccessListener {
                            callback(true, "Unfollowed user")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to update followers")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun isFollowingUser(
        currentUserId: String,
        targetUserId: String,
        callback: (Boolean, String, Boolean) -> Unit
    ) {
        usersRef.child(currentUserId).child("following")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val following = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { following.add(it) }
                        }
                    }
                    callback(true, "Check completed", following.contains(targetUserId))
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, false)
                }
            })
    }

    // ==================== RECENT ACTIVITY ====================

    override fun getRecentlyPlayed(
        userId: String,
        limit: Int,
        callback: (Boolean, String, List<String>) -> Unit
    ) {
        usersRef.child(userId).child("recentlyPlayed").limitToLast(limit)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val recent = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { recent.add(it) }
                        }
                    }
                    callback(true, "Recently played fetched", recent.reversed())
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun addToRecentlyPlayed(
        userId: String,
        songId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("recentlyPlayed")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val recent = mutableListOf<String>()
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            item.getValue(String::class.java)?.let { recent.add(it) }
                        }
                    }

                    // Remove if already exists (to move to end)
                    recent.remove(songId)
                    recent.add(songId)

                    // Keep only last 50
                    if (recent.size > 50) {
                        recent.removeAt(0)
                    }

                    usersRef.child(userId).child("recentlyPlayed").setValue(recent)
                        .addOnSuccessListener {
                            callback(true, "Added to recently played")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Failed to add")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun clearRecentlyPlayed(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("recentlyPlayed").setValue(emptyList<String>())
            .addOnSuccessListener {
                callback(true, "Recently played cleared")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to clear")
            }
    }

    // ==================== USER PREFERENCES ====================

    override fun updatePreferences(
        userId: String,
        preferences: Map<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        usersRef.child(userId).child("preferences").updateChildren(preferences)
            .addOnSuccessListener {
                callback(true, "Preferences updated")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update preferences")
            }
    }

    override fun getPreferences(
        userId: String,
        callback: (Boolean, String, Map<String, Any>?) -> Unit
    ) {
        usersRef.child(userId).child("preferences")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val prefs = snapshot.value as? Map<String, Any>
                    callback(true, "Preferences fetched", prefs)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    // ==================== OFFLINE/CACHE ====================

    override fun enableOfflineMode(userId: String) {
        usersRef.child(userId).keepSynced(true)
        Log.d(TAG, "Offline mode enabled for user: $userId")
    }

    override fun disableOfflineMode(userId: String) {
        usersRef.child(userId).keepSynced(false)
        Log.d(TAG, "Offline mode disabled for user: $userId")
    }

    override fun syncOfflineData(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        // Firebase automatically syncs when connection is restored
        // This is just a placeholder for any custom sync logic
        callback(true, "Sync completed")
    }
}