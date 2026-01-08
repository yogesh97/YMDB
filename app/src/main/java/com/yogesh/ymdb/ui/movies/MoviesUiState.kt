package com.yogesh.ymdb.ui.movies

import com.yogesh.ymdb.data.local.MovieEntity

sealed class MoviesUiState {
    object Loading : MoviesUiState()
    data class Success(val trending: List<MovieEntity>, val nowPlaying: List<MovieEntity>) : MoviesUiState()
    data class Error(val message: String) : MoviesUiState()
}