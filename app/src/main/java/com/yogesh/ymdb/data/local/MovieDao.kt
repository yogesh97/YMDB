package com.yogesh.ymdb.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isTrending = 1")
    fun getTrendingMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isNowPlaying = 1")
    fun getNowPlayingMovies(): Flow<List<MovieEntity>>

    @Transaction
    suspend fun updateMoviesTransaction(trending: List<MovieEntity>, nowPlaying: List<MovieEntity>) {
        insertMovies(trending)
        insertMovies(nowPlaying)
    }

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity
}