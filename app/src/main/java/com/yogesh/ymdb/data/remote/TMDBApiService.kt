package com.yogesh.ymdb.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiService {
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(): MovieResponse

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDto
}