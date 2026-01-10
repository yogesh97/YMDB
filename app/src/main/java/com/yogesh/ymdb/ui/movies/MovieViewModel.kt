package com.yogesh.ymdb.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogesh.ymdb.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            combine(
                repository.getTrendingMovies(),
                repository.getNowPlayingMovies()
            ) { trending, nowPlaying ->
                if (trending.isNotEmpty() || nowPlaying.isNotEmpty()) {
                    MoviesUiState.Success(trending, nowPlaying)
                } else {
                    MoviesUiState.Loading
                }
            }.catch { e ->
                _uiState.value = MoviesUiState.Error(e.message ?: "Unknown Error")
            }.collect { state ->
                _uiState.value = state
            }
        }

        viewModelScope.launch {
            try {
                repository.refreshMovies()
            } catch (e: Exception) {
                if (_uiState.value is MoviesUiState.Loading) {
                    _uiState.value = MoviesUiState.Error("No Internet Connection")
                }
            }
        }
    }
}