package com.example.assignment2.model

data class GameResultData(
    val id: Long = System.currentTimeMillis(),
    val date: String,
    val finalMoney: Int,
    val totalSavings: Int,
    val totalExpenses: Int,
    val finalScore: Int,
    val performanceRank: String
)