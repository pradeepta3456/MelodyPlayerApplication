package com.example.musicplayerapplication.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.SearchResult
import com.example.musicplayerapplication.model.SearchSongs
import com.example.musicplayerapplication.repository.SearchRepoImpl
import com.example.musicplayerapplication.repository.SearchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
class SearchViewModel(
    private val repository: SearchRepository = SearchRepoImpl()
) : ViewModel() {

    var query = mutableStateOf("")
    var searchResults = mutableStateOf<SearchResult?>(null)
    var recentSearches = mutableStateListOf<String>()
    var isSearching = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    init {
        loadRecentSearches()
    }

    /**
     * Load recent searches
     */
    private fun loadRecentSearches() {
        viewModelScope.launch {
            try {
                recentSearches.addAll(repository.getRecentSearches())
            } catch (e: Exception) {
                errorMessage.value = "Failed to load recent searches"
            }
        }
    }

    /**
     * Perform search
     */
    fun search(searchQuery: String) {
        query.value = searchQuery

        if (searchQuery.isBlank()) {
            searchResults.value = null
            return
        }

        viewModelScope.launch {
            isSearching.value = true
            try {
                delay(300) // Debounce
                searchResults.value = repository.search(searchQuery)

                // Add to recent searches
                if (!recentSearches.contains(searchQuery)) {
                    recentSearches.add(0, searchQuery)
                    if (recentSearches.size > 10) {
                        recentSearches.removeLast()
                    }
                }

                isSearching.value = false
            } catch (e: Exception) {
                errorMessage.value = "Search failed"
                isSearching.value = false
            }
        }
    }

    /**
     * Clear recent searches
     */
    fun clearRecentSearches() {
        recentSearches.clear()
        repository.clearRecentSearches()
    }

    /**
     * Remove specific search from recent
     */
    fun removeRecentSearch(searchQuery: String) {
        recentSearches.remove(searchQuery)
    }

    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage.value = null
    }
}
