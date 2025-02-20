package com.example.memorygame.data

import android.content.Context
import androidx.room.Room

class DatabaseHelper(context: Context) {

    private val db: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).build()

    suspend fun insertRecord(playerName: String, moves: Int, time: Int, errors: Int) {
        val score = Score(playerName = playerName, moves = moves, time = time, errors = errors)
        db.scoreDao().insertRecord(score)
    }

    suspend fun getTopScores(): List<Quadruple<String, Int, Int, Int>> {
        val scores = db.scoreDao().getTopScores()
        return scores.map { score ->
            Quadruple(score.playerName, score.moves, score.time, score.errors)
        }
    }

    companion object
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
