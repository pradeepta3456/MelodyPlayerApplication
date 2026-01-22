package com.example.musicplayerapplication

import android.app.Application
import android.util.Log
import com.example.musicplayerapplication.Utils.AudioPlayer
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Initializes Firebase, App Check, and other global components.
 * This ensures all Firebase services are properly configured before use.
 */
class MusicPlayerApplication : Application() {

    companion object {
        private const val TAG = "MusicPlayerApp"
    }

    override fun onCreate() {
        super.onCreate()

        try {
            // Initialize Firebase explicitly
            val firebaseApp = FirebaseApp.initializeApp(this)
            if (firebaseApp != null) {
                Log.d(TAG, "Firebase initialized successfully")
                Log.d(TAG, "Firebase App Name: ${firebaseApp.name}")
                Log.d(TAG, "Firebase Project ID: ${firebaseApp.options.projectId}")
            } else {
                Log.e(TAG, "Firebase initialization returned null")
            }

            // Enable Firebase Auth persistence
            FirebaseAuth.getInstance().also {
                Log.d(TAG, "Firebase Auth instance obtained")
            }

            // Get Firebase Database reference and enable logging in debug mode
            FirebaseDatabase.getInstance().apply {
                Log.d(TAG, "Firebase Database URL: ${reference.toString()}")

                // Enable offline persistence (helps with network issues)
                setPersistenceEnabled(true)
                Log.d(TAG, "Firebase Database persistence enabled")
            }

            // Configure App Check
            configureAppCheck()

            // Initialize AudioPlayer globally
            AudioPlayer.getInstance(applicationContext)
            Log.d(TAG, "AudioPlayer initialized")

        } catch (e: Exception) {
            Log.e(TAG, "Error during application initialization: ${e.message}", e)
        }
    }

    private fun configureAppCheck() {
        try {
            val appCheck = FirebaseAppCheck.getInstance()

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Using Debug App Check Provider for development")
                appCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
            } else {
                Log.d(TAG, "Using Play Integrity App Check Provider for production")
                appCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
                )
            }

            Log.d(TAG, "Firebase App Check configured successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to configure App Check: ${e.message}", e)
            // Don't throw - allow app to continue even if App Check fails
        }
    }
}
