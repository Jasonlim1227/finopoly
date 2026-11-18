package com.example.assignment2.data

import com.example.assignment2.R
import com.example.assignment2.model.Tile
import com.example.assignment2.model.TileType

object TileRepository {
    fun getBoardTiles(): List<Tile> {
        return listOf(
            Tile(0, "START", TileType.START, R.drawable.ic_start_flag, R.color.cat_start, "Collect RM 500 bonus as you pass START!"),
            Tile(1, "Food", TileType.FOOD, R.drawable.ic_food, R.color.cat_food, "Manage your monthly food budget.", 200),
            Tile(2, "Transport", TileType.TRANSPORT, R.drawable.ic_transport, R.color.cat_transport, "Choose public transit or private car.", 150),
            Tile(3, "Quiz", TileType.QUIZ, R.drawable.ic_quiz, R.color.cat_scenario, "Random financial scenario quiz!"),
            Tile(4, "Rent", TileType.RENT, R.drawable.ic_rent, R.color.cat_rent, "Monthly house rent payment.", 600),
            Tile(5, "Bills", TileType.BILLS, R.drawable.ic_bills, R.color.cat_bills, "Electricity & Utility bills.", 180),
            Tile(6, "Quiz", TileType.QUIZ, R.drawable.ic_quiz, R.color.cat_scenario, "Random financial scenario quiz!"),
            Tile(7, "Savings", TileType.SAVINGS, R.drawable.ic_savings, R.color.cat_savings, "Put money into emergency savings.", 300),
            Tile(8, "Quiz", TileType.QUIZ, R.drawable.ic_quiz, R.color.cat_scenario, "Random financial scenario quiz!"),
            Tile(9, "Education", TileType.EDUCATION, R.drawable.ic_education, R.color.cat_education, "Invest in self-education & books.", 150),
            Tile(10, "Entertainment", TileType.ENTERTAINMENT, R.drawable.ic_entertainment, R.color.cat_entertainment, "Movie & weekend hangouts.", 120),
            Tile(11, "Quiz", TileType.QUIZ, R.drawable.ic_quiz, R.color.cat_scenario, "Random financial scenario quiz!"),
            Tile(12, "Quiz", TileType.QUIZ, R.drawable.ic_quiz, R.color.cat_scenario, "Random financial scenario quiz!"),
            Tile(13, "Quiz", TileType.QUIZ, R.drawable.ic_quiz, R.color.cat_scenario, "Random financial scenario quiz!"),
            Tile(14, "Emergency Savings", TileType.SAVINGS, R.drawable.ic_savings, R.color.cat_savings, "Grow emergency backup fund.", 250),
            Tile(15, "Quiz", TileType.QUIZ, R.drawable.ic_quiz, R.color.cat_scenario, "Random financial scenario quiz!")
        )
    }
}