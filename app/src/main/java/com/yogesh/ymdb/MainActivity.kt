package com.yogesh.ymdb

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogesh.ymdb.data.repository.MovieRepository
import com.yogesh.ymdb.databinding.ActivityMainBinding
import com.yogesh.ymdb.network.RetrofitClient
import com.yogesh.ymdb.ui.movies.MovieAdapter
import com.yogesh.ymdb.ui.movies.MovieViewModel
import com.yogesh.ymdb.ui.movies.MovieViewModelFactory
import com.yogesh.ymdb.ui.movies.MoviesUiState
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MovieViewModel

    val trendingAdapter = MovieAdapter { movie ->
    }

    val nowPlayingAdapter = MovieAdapter { movie ->
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

        val database = androidx.room.Room.databaseBuilder(
            applicationContext,
            com.yogesh.ymdb.data.local.YMDBDatabase::class.java, "ymdb_db"
        ).build()

        val apiService = RetrofitClient.instance
        val repository = MovieRepository(apiService, database.movieDao())

        val factory = MovieViewModelFactory(repository)
        viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[MovieViewModel::class.java]

        observeUiState()

        setupRecyclerViews()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is MoviesUiState.Loading -> {  }
                    is MoviesUiState.Success -> {
                        trendingAdapter.submitList(state.trending)
                        nowPlayingAdapter.submitList(state.nowPlaying)
                    }
                    is MoviesUiState.Error -> {  }
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.apply {
            rvTrending.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = trendingAdapter
            }

            rvNowPlaying.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = nowPlayingAdapter
            }
        }
    }
}