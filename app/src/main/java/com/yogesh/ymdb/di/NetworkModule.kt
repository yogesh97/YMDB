package com.yogesh.ymdb.di

import android.content.Context
import androidx.room.Room
import com.yogesh.ymdb.data.local.YMDBDatabase
import com.yogesh.ymdb.data.local.MovieDao
import com.yogesh.ymdb.data.remote.AuthInterceptor
import com.yogesh.ymdb.data.remote.TMDBApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideTMDBApi(okHttpClient: OkHttpClient): TMDBApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): YMDBDatabase {
        return Room.databaseBuilder(
            context,
            YMDBDatabase::class.java,
            "ymdb_database"
        )
            .build()
    }

    @Provides
    fun provideMovieDao(database: YMDBDatabase): MovieDao {
        return database.movieDao()
    }
}
