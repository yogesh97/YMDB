package com.yogesh.ymdb.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogesh.ymdb.data.local.MovieEntity
import com.yogesh.ymdb.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SavedMoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    val savedMovies: StateFlow<List<MovieEntity>> = repository.getBookmarkedMovies()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
