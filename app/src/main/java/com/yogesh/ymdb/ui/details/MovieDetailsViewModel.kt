package com.yogesh.ymdb.ui.details

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
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movieDetails = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val movieDetails: StateFlow<MovieDetailsUiState> = _movieDetails.asStateFlow()

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val movie = repository.getMovieById(movieId)
                _movieDetails.value = MovieDetailsUiState.Success(movie)
            } catch (e: Exception) {
                _movieDetails.value = MovieDetailsUiState.Error(e.message ?: "Failed to load details")
            }
        }
    }
}

sealed class MovieDetailsUiState {
    object Loading : MovieDetailsUiState()
    data class Success(val movie: MovieEntity) : MovieDetailsUiState()
    data class Error(val message: String) : MovieDetailsUiState()
}
