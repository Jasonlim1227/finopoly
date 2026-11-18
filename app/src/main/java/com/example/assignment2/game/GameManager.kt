package com.example.assignment2.game

import com.example.assignment2.data.TileRepository
import com.example.assignment2.model.QuizChoice
import com.example.assignment2.model.Tile
import com.example.assignment2.model.TileType

class GameManager {
    val initialIncome: Int = 2500
    val startBonus: Int = 500

    var money: Int = 2500
        private set
    var savings: Int = 0
        private set
    var expenses: Int = 0
        private set
    var score: Int = 0
        private set

    var currentPosition: Int = 0
        private set
    var turnCount: Int = 1
        private set
    val maxTurns: Int = 15

    val boardTiles: List<Tile> = TileRepository.getBoardTiles()

    val categoryExpenses = mutableMapOf<TileType, Int>(
        TileType.FOOD to 0,
        TileType.TRANSPORT to 0,
        TileType.RENT to 0,
        TileType.BILLS to 0,
        TileType.EDUCATION to 0,
        TileType.SAVINGS to 0,
        TileType.ENTERTAINMENT to 0
    )

    fun rollDice(): Int {
        return (1..6).random()
    }

    fun movePlayer(steps: Int): Pair<Tile, Boolean> {
        val oldPos = currentPosition
        currentPosition = (currentPosition + steps) % boardTiles.size

        val passedStart = (oldPos + steps) >= boardTiles.size
        if (passedStart) {
            // Collect start bonus (RM 500)
            money += startBonus
            score += 10
            clearDebtWithSavings()
        }

        return Pair(boardTiles[currentPosition], passedStart)
    }

    fun advanceTurn() {
        if (turnCount <= maxTurns) {
            turnCount++
        }
    }

    fun clearDebtWithSavings() {
        // If money is in debt and savings have value, savings pays up money's debt
        if (money < 0 && savings > 0) {
            val debtAmount = -money
            val debtPaidFromSavings = minOf(savings, debtAmount)
            savings -= debtPaidFromSavings
            money += debtPaidFromSavings
        }
        savings = savings.coerceAtLeast(0)
    }

    fun processPayment(amount: Int) {
        if (amount <= 0) return

        expenses += amount
        money -= amount
        clearDebtWithSavings()
    }

    fun applyCategoryDecision(tileType: TileType, amountSpent: Int, amountSaved: Int, choiceType: ChoiceType) {
        if (amountSpent > 0) {
            processPayment(amountSpent)
            categoryExpenses[tileType] = (categoryExpenses[tileType] ?: 0) + amountSpent
        }
        if (amountSaved > 0) {
            savings += amountSaved
            categoryExpenses[TileType.SAVINGS] = (categoryExpenses[TileType.SAVINGS] ?: 0) + amountSaved
            clearDebtWithSavings()
        }

        clearDebtWithSavings()

        // Scoring rules:
        // Saving money -> +10 points
        // Staying within budget -> +20 points
        // Unnecessary spending -> -10 points
        // Exceeding budget -> -20 points
        when (choiceType) {
            ChoiceType.SAVING -> score += 10
            ChoiceType.WISE_BUDGET -> score += 20
            ChoiceType.UNNECESSARY -> score -= 10
            ChoiceType.EXCEEDING_BUDGET -> score -= 20
        }
        // Score has no upper limit, min 0
        score = score.coerceAtLeast(0)
    }

    fun applyQuizChoice(choice: QuizChoice) {
        if (choice.moneyDelta < 0) {
            processPayment(-choice.moneyDelta)
        } else if (choice.moneyDelta > 0) {
            money += choice.moneyDelta
            clearDebtWithSavings()
        }

        if (choice.savingsDelta > 0) {
            savings += choice.savingsDelta
            clearDebtWithSavings()
        } else if (choice.savingsDelta < 0) {
            val amountToDeduct = minOf(savings, -choice.savingsDelta)
            savings -= amountToDeduct
            val remainingUncovered = (-choice.savingsDelta) - amountToDeduct
            if (remainingUncovered > 0) {
                processPayment(remainingUncovered)
            } else {
                clearDebtWithSavings()
            }
        }

        clearDebtWithSavings()

        score += choice.scoreDelta
        score = score.coerceAtLeast(0)
    }

    fun getSavingsBonusPercent(): Int {
        return when {
            score >= 300 -> 15
            score >= 200 -> 10
            score >= 100 -> 5
            else -> 0
        }
    }

    fun calculateFinalSavings(): Int {
        val bonusPercent = getSavingsBonusPercent()
        return savings + (savings * bonusPercent / 100)
    }

    fun isGameOver(): Boolean {
        return turnCount > maxTurns || money <= -1000
    }

    fun displayTurn(): Int {
        return turnCount.coerceAtMost(maxTurns)
    }

    fun getPerformanceRank(): String {
        return when {
            score >= 250 -> "Master Financial Genius!"
            score >= 180 -> "Excellent Budget Manager!"
            score >= 100 -> "Wise Financial Planner!"
            score >= 50 -> "Moderate Budgeter"
            else -> "Needs Financial Improvement"
        }
    }

    enum class ChoiceType {
        SAVING,
        WISE_BUDGET,
        UNNECESSARY,
        EXCEEDING_BUDGET
    }
}