package com.yogesh.ymdb.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApiService {
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(): MovieResponse
}