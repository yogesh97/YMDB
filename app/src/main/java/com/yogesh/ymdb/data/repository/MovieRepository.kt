package com.yogesh.ymdb.data.repository

import android.util.Log
import com.yogesh.ymdb.data.local.MovieDao
import com.yogesh.ymdb.data.local.MovieEntity
import com.yogesh.ymdb.data.mapper.toEntity
import com.yogesh.ymdb.data.remote.TMDBApiService
import com.yogesh.ymdb.util.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MovieRepository @Inject constructor(
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
                Log.e(TAG, "refreshMovies: Exception:", e)
            }
        }
    }

    private suspend fun updateLocalDatabase(trending: List<MovieEntity>, nowPlaying: List<MovieEntity>) {
        movieDao.updateMoviesTransaction(trending, nowPlaying)
    }

    fun getMovieById(movieId: Int): Flow<MovieEntity> {
        return movieDao.getMovieById(movieId)
    }

    suspend fun toggleBookmark(movieId: Int, currentStatus: Boolean) {
        movieDao.updateBookmarkStatus(movieId, !currentStatus)
    }

    fun getBookmarkedMovies() = movieDao.getBookmarkedMovies()

    suspend fun searchMovies(query: String): List<MovieEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchMovies(query)
                response.results.map { it.toEntity(isTrending = false, isNowPlaying = false) }
            } catch (e: Exception) {
                Log.e(TAG, "searchMovies: Exception:", e)
                emptyList()
            }
        }
    }

    suspend fun fetchAndSaveMovieDetails(movieId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val movieDto = apiService.getMovieDetails(movieId)
                val entity = movieDto.toEntity(isTrending = false, isNowPlaying = false)
                movieDao.insertMovies(listOf(entity))
            } catch (e: Exception) {
                Log.e(TAG, "fetchAndSaveMovieDetails: Error", e)
                throw e
            }
        }
    }
}