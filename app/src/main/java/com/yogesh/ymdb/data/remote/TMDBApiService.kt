package com.yogesh.ymdb.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApiService {
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String
    ): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String
    ): MovieResponse
}