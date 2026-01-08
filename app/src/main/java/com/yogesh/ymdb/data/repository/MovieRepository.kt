package com.yogesh.ymdb.data.repository

import android.util.Log
import com.yogesh.ymdb.data.local.MovieDao
import com.yogesh.ymdb.data.local.MovieEntity
import com.yogesh.ymdb.data.mapper.toEntity
import com.yogesh.ymdb.data.remote.TMDBApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext


class MovieRepository(
    private val apiService: TMDBApiService,
    private val movieDao: MovieDao
) {
    fun getTrendingMovies() = movieDao.getTrendingMovies()
    fun getNowPlayingMovies() = movieDao.getNowPlayingMovies()

    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            try {
                coroutineScope {
                    val trendingDeferred = async { apiService.getTrendingMovies() }
                    val nowPlayingDeferred = async { apiService.getNowPlayingMovies() }
                    val trendingResponse = trendingDeferred.await()
                    val nowPlayingResponse = nowPlayingDeferred.await()

                    val trendingEntities = trendingResponse.results.map {
                        it.toEntity(isTrending = true, isNowPlaying = false)
                    }
                    val nowPlayingEntities = nowPlayingResponse.results.map {
                        it.toEntity(isTrending = false, isNowPlaying = true)
                    }
                    updateLocalDatabase(trendingEntities, nowPlayingEntities)
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "refreshMovies: Exception:", e)
            }
        }
    }

    private suspend fun updateLocalDatabase(trending: List<MovieEntity>, nowPlaying: List<MovieEntity>) {
        movieDao.updateMoviesTransaction(trending, nowPlaying)
    }
}