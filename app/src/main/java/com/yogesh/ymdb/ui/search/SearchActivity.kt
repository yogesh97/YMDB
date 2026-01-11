package com.yogesh.ymdb.ui.search

import android.os.Bundle
import android.util.Log
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
import com.yogesh.ymdb.util.TAG
import com.yogesh.ymdb.util.applySystemBarsPadding
import com.yogesh.ymdb.util.openMovieDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

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
                    viewModel.search(query)
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "onQueryTextChange: newText=$newText")
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter { movie ->
            openMovieDetails(movie)
        }
        binding.rvSearch.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collect { movies ->
                    adapter.submitList(movies)
                    binding.tvNoResults.isVisible = movies.isEmpty()
                }
            }
        }
    }
}
