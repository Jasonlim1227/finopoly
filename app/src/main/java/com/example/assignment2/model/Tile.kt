package com.example.assignment2.model

enum class TileType {
    START,
    FOOD,
    TRANSPORT,
    RENT,
    BILLS,
    EDUCATION,
    SAVINGS,
    ENTERTAINMENT,
    QUIZ
}

data class Tile(
    val id: Int,
    val name: String,
    val type: TileType,
    val iconRes: Int,
    val categoryColorRes: Int,
    val description: String,
    val baseCost: Int = 0
)