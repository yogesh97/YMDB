package com.yogesh.ymdb.util

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

fun View.applySystemBarsPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }
}