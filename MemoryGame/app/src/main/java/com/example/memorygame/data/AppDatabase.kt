package com.example.memorygame.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Score::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scoreDao(): ScoreDao

    companion object {
        const val DATABASE_NAME = "memory_game.db"
    }
}
