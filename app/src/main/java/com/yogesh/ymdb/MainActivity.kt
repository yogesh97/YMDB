package com.yogesh.ymdb

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yogesh.ymdb.databinding.ActivityMainBinding
import com.yogesh.ymdb.ui.details.MovieDetailsActivity
import com.yogesh.ymdb.ui.movies.MovieAdapter
import com.yogesh.ymdb.ui.movies.MovieViewModel
import com.yogesh.ymdb.ui.movies.MoviesUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MovieViewModel by viewModels()

    val trendingAdapter = MovieAdapter { movie ->
        navigateToDetails(movie.id)
    }

    val nowPlayingAdapter = MovieAdapter { movie ->
        navigateToDetails(movie.id)
    }

    private fun navigateToDetails(movieId: Int) {
        val intent = Intent(this, MovieDetailsActivity::class.java).apply {
            putExtra("EXTRA_MOVIE_ID", movieId)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeUiState()

        setupRecyclerViews()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state is MoviesUiState.Loading
                    when (state) {
                        is MoviesUiState.Success -> {
                            trendingAdapter.submitList(state.trending)
                            nowPlayingAdapter.submitList(state.nowPlaying)
                        }
                        is MoviesUiState.Error -> {
                            Snackbar.make(
                                binding.root, state.message,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.apply {
            rvTrending.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = trendingAdapter
            }

            rvNowPlaying.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = nowPlayingAdapter
            }
        }
    }
}