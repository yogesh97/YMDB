package com.yogesh.ymdb.ui.details

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.yogesh.ymdb.R
import com.yogesh.ymdb.databinding.ActivityMovieDetailsBinding
import com.yogesh.ymdb.util.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val movieId = intent.getIntExtra("EXTRA_MOVIE_ID", -1)
        if (movieId != -1) {
            viewModel.fetchMovieDetails(movieId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetails.collect { state ->
                    binding.progressBar.isVisible = state is MovieDetailsUiState.Loading
                    when (state) {
                        is MovieDetailsUiState.Success -> {
                            val movie = state.movie
                            binding.tvTitle.text = movie.title
                            binding.tvOverview.text = movie.overview

                            Glide.with(this@MovieDetailsActivity)
                                .load("https://image.tmdb.org/t/p/w780${movie.backdropPath}")
                                .into(binding.ivBackdrop)

                            if (movie.isBookmarked) {
                                binding.btnBookmark.setImageResource(R.drawable.bookmark_added)
                            } else {
                                binding.btnBookmark.setImageResource(R.drawable.bookmark_add)
                            }
                            binding.btnBookmark.setOnClickListener {
                                viewModel.toggleBookmark(movie)
                            }
                        }
                        is MovieDetailsUiState.Error -> {
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                this@MovieDetailsActivity,
                                "Error loading movie detail",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(
                                TAG,
                                "observeViewModel: error=" + state.message
                            )
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
