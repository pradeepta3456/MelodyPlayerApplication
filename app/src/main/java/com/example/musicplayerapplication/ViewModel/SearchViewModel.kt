package com.example.musicplayerapplication.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.SearchResult
import com.example.musicplayerapplication.model.SearchSongs
import com.example.musicplayerapplication.repository.SearchRepoImpl
import com.example.musicplayerapplication.repository.SearchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository = SearchRepoImpl()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _songs = MutableStateFlow<List<SearchSongs>>(emptyList())
    val songs: StateFlow<List<SearchSongs>> = _songs.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _searchResults = MutableStateFlow<SearchResult?>(null)
    val searchResults: StateFlow<SearchResult?> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadRecentSearches()
        loadTrendingSongs()
    }

    /**
     * Load recent searches
     */
    private fun loadRecentSearches() {
        viewModelScope.launch {
            try {
                _recentSearches.value = repository.getRecentSearches()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recent searches"
            }
        }
    }

    /**
     * Load trending songs
     */
    private fun loadTrendingSongs() {
        viewModelScope.launch {
            try {
                // Load some default trending songs
                // You can implement this in your repository
                _songs.value = listOf(
                    SearchSongs(1, "Trending Song 1", "Artist 1", "1.2M"),
                    SearchSongs(2, "Trending Song 2", "Artist 2", "980K"),
                    SearchSongs(3, "Trending Song 3", "Artist 3", "856K"),
                    SearchSongs(4, "Trending Song 4", "Artist 4", "743K"),
                    SearchSongs(5, "Trending Song 5", "Artist 5", "621K")
                )
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load trending songs"
            }
        }
    }

    /**
     * Handle query change
     */
    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isNotBlank()) {
            search(newQuery)
        } else {
            _searchResults.value = null
        }
    }

    /**
     * Handle recent search click
     */
    fun onRecentSearchClick(searchQuery: String) {
        _query.value = searchQuery
        search(searchQuery)
    }

    /**
     * Perform search
     */
    private fun search(searchQuery: String) {
        if (searchQuery.isBlank()) {
            _searchResults.value = null
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            try {
                delay(300) // Debounce
                _searchResults.value = repository.search(searchQuery)

                // Add to recent searches
                val currentRecent = _recentSearches.value.toMutableList()
                if (!currentRecent.contains(searchQuery)) {
                    currentRecent.add(0, searchQuery)
                    if (currentRecent.size > 10) {
                        currentRecent.removeLast()
                    }
                    _recentSearches.value = currentRecent
                }

                _isSearching.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Search failed"
                _isSearching.value = false
            }
        }
    }

    /**
     * Clear recent searches
     */
    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        repository.clearRecentSearches()
    }

    /**
     * Remove specific search from recent
     */
    fun removeRecentSearch(searchQuery: String) {
        _recentSearches.value = _recentSearches.value.filter { it != searchQuery }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}