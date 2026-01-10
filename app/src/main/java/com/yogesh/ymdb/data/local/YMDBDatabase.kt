package com.yogesh.ymdb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class], version = 3, exportSchema = false)
abstract class YMDBDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}