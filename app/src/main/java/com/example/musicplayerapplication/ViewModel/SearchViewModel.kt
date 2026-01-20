package com.example.musicplayerapplication.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapplication.model.SearchSongs
import com.example.musicplayerapplication.repository.SearchRepoImpl
import com.example.musicplayerapplication.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.emptyList
class SearchViewModel(
    private val repo: SearchRepository = SearchRepoImpl()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    val songs: StateFlow<List<SearchSongs>> =
        _query
            .flatMapLatest { repo.searchSongs(it) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery

        if (newQuery.isNotBlank()) {
            _recentSearches.value =
                (_recentSearches.value - newQuery).toMutableList()
                    .apply {
                        add(0, newQuery)
                    }
                    .take(5)
        }
    }

    fun onRecentSearchClick(search: String) {
        _query.value = search
    }
}
