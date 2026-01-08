package com.yogesh.ymdb.data.repository

import android.util.Log
import com.yogesh.ymdb.data.local.MovieDao
import com.yogesh.ymdb.data.mapper.toEntity
import com.yogesh.ymdb.data.remote.TMDBApiService


class MovieRepository(
    private val apiService: TMDBApiService,
    private val movieDao: MovieDao
) {
    fun getTrendingMovies() = movieDao.getTrendingMovies()

    fun getNowPlayingMovies() = movieDao.getNowPlayingMovies()

    suspend fun refreshMovies() {
        try {
            val trendingResponse = apiService.getTrendingMovies()
            val trendingEntities = trendingResponse.results.map {
                it.toEntity(isTrending = true, isNowPlaying = false)
            }
            movieDao.insertMovies(trendingEntities)

            val nowPlayingResponse = apiService.getNowPlayingMovies()
            val nowPlayingEntities = nowPlayingResponse.results.map {
                it.toEntity(isTrending = false, isNowPlaying = true)
            }
            movieDao.insertMovies(nowPlayingEntities)
        } catch (e: Exception) {
            Log.e("MovieRepository", "refreshMovies: Exception:", e)
        }
    }
}