package com.example.memorygame.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val moves: Int,
    val time: Int,
    val errors: Int
)
