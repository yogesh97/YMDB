package com.yogesh.ymdb.data.mapper

import com.yogesh.ymdb.data.local.MovieEntity
import com.yogesh.ymdb.data.remote.MovieDto

fun MovieDto.toEntity(isTrending: Boolean, isNowPlaying: Boolean): MovieEntity {
    return MovieEntity(
        id = this.id,
        title = this.title,
        overview = this.overview,
        posterPath = this.posterPath,
        releaseDate = this.releaseDate,
        isTrending = isTrending,
        isNowPlaying = isNowPlaying
    )
}