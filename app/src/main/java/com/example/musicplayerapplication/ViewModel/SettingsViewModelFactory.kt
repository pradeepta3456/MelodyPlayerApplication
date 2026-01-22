package com.example.musicplayerapplication.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayerapplication.repository.SettingsRepoImpl

/**
 * Factory for creating SettingsViewModel with dependencies
 */
class SettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                repository = SettingsRepoImpl(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
