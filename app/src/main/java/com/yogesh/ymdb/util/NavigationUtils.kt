package com.yogesh.ymdb.util

import android.content.Context
import android.content.Intent
import com.yogesh.ymdb.data.local.MovieEntity
import com.yogesh.ymdb.ui.details.MovieDetailsActivity

fun Context.openMovieDetails(movie: MovieEntity) {
    val intent = Intent(this, MovieDetailsActivity::class.java).apply {
        putExtra("EXTRA_MOVIE_ID", movie.id)
    }
    startActivity(intent)
}

val Any.TAG: String
    get() = this.javaClass.simpleName