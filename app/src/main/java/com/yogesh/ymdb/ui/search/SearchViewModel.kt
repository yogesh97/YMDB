package com.yogesh.ymdb.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogesh.ymdb.data.local.MovieEntity
import com.yogesh.ymdb.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: MovieRepository) :
    ViewModel() {
    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchUiState.Initial
            return
        }

        viewModelScope.launch {
            _searchState.value = SearchUiState.Loading
            val results = repository.searchMovies(query)
            _searchState.value = SearchUiState.Success(results)
        }
    }

    fun clearResults() {
        _searchState.value = SearchUiState.Initial
    }
}

sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val movies: List<MovieEntity>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
