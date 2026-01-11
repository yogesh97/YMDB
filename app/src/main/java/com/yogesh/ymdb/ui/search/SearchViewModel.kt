package com.yogesh.ymdb.ui.search


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogesh.ymdb.data.local.MovieEntity
import com.yogesh.ymdb.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: MovieRepository) :
    ViewModel() {
    private val _searchResults = MutableStateFlow<List<MovieEntity>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchMovies(query)
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList<MovieEntity>()
    }
}
