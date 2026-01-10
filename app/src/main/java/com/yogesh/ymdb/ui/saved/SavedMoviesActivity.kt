package com.yogesh.ymdb.ui.saved

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.yogesh.ymdb.databinding.ActivitySavedMoviesBinding
import com.yogesh.ymdb.ui.details.MovieDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yogesh.ymdb.R
import com.yogesh.ymdb.ui.movies.MovieAdapter

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter { movie ->
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("EXTRA_MOVIE_ID", movie.id)
            }
            startActivity(intent)
        }
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
