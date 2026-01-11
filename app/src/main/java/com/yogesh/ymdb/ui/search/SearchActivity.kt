package com.yogesh.ymdb.ui.search

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yogesh.ymdb.databinding.ActivitySearchBinding
import com.yogesh.ymdb.ui.movies.MovieAdapter
import com.yogesh.ymdb.util.applySystemBarsPadding
import com.yogesh.ymdb.util.openMovieDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MovieAdapter
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding()

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchJob?.cancel()
                    viewModel.search(query)
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()

                if (!newText.isNullOrBlank() && newText.length >= 2) {
                    searchJob = lifecycleScope.launch {
                        delay(500L)
                        viewModel.search(newText)
                    }

                } else if (newText.isNullOrBlank()) {
                    viewModel.clearResults()
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter { movie -> openMovieDetails(movie) }
        binding.rvSearch.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collect { state ->
                    when (state) {
                        is SearchUiState.Initial -> {
                            binding.tvNoResults.isVisible = false
                            adapter.submitList(emptyList())
                        }
                        is SearchUiState.Success -> {
                            adapter.submitList(state.movies)
                            binding.tvNoResults.isVisible = state.movies.isEmpty()
                        }
                        is SearchUiState.Error -> {
                            binding.tvNoResults.isVisible = false
                            Toast.makeText(this@SearchActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
