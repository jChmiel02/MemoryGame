package com.example.memorygame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {

    @Insert
    suspend fun insertRecord(score: Score)

    @Query("SELECT * FROM scores ORDER BY moves ASC, time ASC, errors ASC LIMIT 10")
    suspend fun getTopScores(): List<Score>
}
