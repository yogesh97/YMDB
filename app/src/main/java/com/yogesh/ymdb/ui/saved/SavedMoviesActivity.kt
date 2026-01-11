package com.yogesh.ymdb.ui.saved

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.yogesh.ymdb.databinding.ActivitySavedMoviesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.enableEdgeToEdge
import com.yogesh.ymdb.ui.movies.MovieAdapter
import com.yogesh.ymdb.util.applySystemBarsPadding
import com.yogesh.ymdb.util.openMovieDetails

@AndroidEntryPoint
class SavedMoviesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedMoviesBinding
    private val viewModel: SavedMoviesViewModel by viewModels()

    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySavedMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        binding.main.applySystemBarsPadding()

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter { movie -> openMovieDetails(movie) }
        binding.rvSavedMovies.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.savedMovies.collect { movies ->
                adapter.submitList(movies)
                binding.tvEmptyState.isVisible = movies.isEmpty()
            }
        }
    }
}
