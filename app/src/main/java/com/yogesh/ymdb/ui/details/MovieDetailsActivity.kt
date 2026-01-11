package com.yogesh.ymdb.ui.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.yogesh.ymdb.R
import com.yogesh.ymdb.databinding.ActivityMovieDetailsBinding
import com.yogesh.ymdb.util.TAG
import com.yogesh.ymdb.util.applySystemBarsPadding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        binding.main.applySystemBarsPadding()

        var movieId = intent.getIntExtra("EXTRA_MOVIE_ID", -1)

        val data = intent.data
        if (data != null && data.pathSegments.size >= 2) {
            val deepLinkId = data.lastPathSegment?.toIntOrNull()
            if (deepLinkId != null) {
                movieId = deepLinkId
            }
        }

        if (movieId != -1) {
            viewModel.fetchMovieDetails(movieId)
        } else {
            Toast.makeText(this, "Invalid Movie ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_share -> {
                    shareMovie()
                    true
                }
                else -> false
            }
        }
    }

    private fun shareMovie() {
        val state = viewModel.movieDetails.value
        if (state is MovieDetailsUiState.Success) {
            val movie = state.movie
            val movieUrl = "https://www.themoviedb.org/movie/${movie.id}"
            val shareText = "Check out this movie: ${movie.title}\n$movieUrl"

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share movie via")
            startActivity(shareIntent)
        } else {
            Toast.makeText(this, "Movie details not ready to share", Toast.LENGTH_SHORT).show()
        }
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
