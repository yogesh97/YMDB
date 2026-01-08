package com.yogesh.ymdb.data.repository

import android.util.Log
import com.yogesh.ymdb.data.local.MovieDao
import com.yogesh.ymdb.data.local.MovieEntity
import com.yogesh.ymdb.data.mapper.toEntity
import com.yogesh.ymdb.data.remote.TMDBApiService
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val apiService: TMDBApiService,
    private val movieDao: MovieDao
) {
    private val API_KEY = "fa9fe243e86906131df3ad4431c551ed"

    fun getTrendingMovies(): Flow<List<MovieEntity>> = movieDao.getTrendingMovies()

    fun getNowPlayingMovies(): Flow<List<MovieEntity>> = movieDao.getNowPlayingMovies()

    suspend fun refreshMovies() {
        try {
            val trendingResponse = apiService.getTrendingMovies(API_KEY)
            val trendingEntities = trendingResponse.results.map {
                it.toEntity(isTrending = true, isNowPlaying = false)
            }
            movieDao.insertMovies(trendingEntities)

            val nowPlayingResponse = apiService.getNowPlayingMovies(API_KEY)
            val nowPlayingEntities = nowPlayingResponse.results.map {
                it.toEntity(isTrending = false, isNowPlaying = true)
            }
            movieDao.insertMovies(nowPlayingEntities)
        } catch (e: Exception) {
            Log.e("MovieRepository", "refreshMovies: Exception:", e)
        }
    }
}