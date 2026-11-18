package com.example.assignment2.model

data class QuizChoice(
    val text: String,
    val moneyDelta: Int,
    val savingsDelta: Int,
    val scoreDelta: Int,
    val isWiseChoice: Boolean,
    val feedback: String
)

data class QuizScenario(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val choices: List<QuizChoice>
)