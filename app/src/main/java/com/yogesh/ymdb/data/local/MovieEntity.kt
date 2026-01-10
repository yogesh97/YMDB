package com.yogesh.ymdb.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val isTrending: Boolean = false,
    val isNowPlaying: Boolean = false,
    val isBookmarked: Boolean = false
)