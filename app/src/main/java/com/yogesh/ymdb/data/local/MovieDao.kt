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
    fun getMovieById(movieId: Int): Flow<MovieEntity>

    @Query("UPDATE movies SET isBookmarked = :isBookmarked WHERE id = :movieId")
    suspend fun updateBookmarkStatus(movieId: Int, isBookmarked: Boolean)

    @Query("SELECT * FROM movies WHERE isBookmarked = 1")
    fun getBookmarkedMovies(): Flow<List<MovieEntity>>
}