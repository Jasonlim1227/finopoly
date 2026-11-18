package com.example.assignment2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.assignment2.data.QuizRepository
import com.example.assignment2.databinding.ActivityGameBinding
import com.example.assignment2.databinding.DialogCategoryChoiceBinding
import com.example.assignment2.databinding.DialogQuizChoiceBinding
import com.example.assignment2.game.GameManager
import com.example.assignment2.model.QuizScenario
import com.example.assignment2.model.Tile
import com.example.assignment2.model.TileType
import com.example.assignment2.sound.SoundManager

class GameActivity : BaseActivity() {

    private lateinit var binding: ActivityGameBinding
    private val gameManager = GameManager()
    private val handler = Handler(Looper.getMainLooper())
    private var isRolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        com.example.assignment2.data.QuizRepository.resetSession()
        binding.boardView.setTiles(gameManager.boardTiles)
        binding.boardView.setPlayerPosition(0, false)

        updateHUD()
        updateTileInfoDisplay(gameManager.boardTiles[0])
        updateSoundToggleIcon()

        binding.ibSoundToggle.setOnClickListener {
            val bgmOn = SoundManager.toggleBgm(this)
            updateSoundToggleIcon()
            val msg = if (bgmOn) "Music Enabled" else "Music Muted"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnRollDice.setOnClickListener {
            if (!isRolling) {
                SoundManager.playClickSound()
                rollDiceAndMove()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SoundManager.startBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        SoundManager.stopBackgroundMusic()
    }

    private fun updateSoundToggleIcon() {
        if (!SoundManager.isBgmEnabled) {
            binding.ibSoundToggle.setImageResource(R.drawable.ic_sound_off)
        } else {
            binding.ibSoundToggle.setImageResource(R.drawable.ic_sound_on)
        }
    }

    private fun updateHUD() {
        val currentTurn = gameManager.displayTurn()
        binding.tvTurnCount.text = getString(R.string.label_turn, currentTurn, gameManager.maxTurns)
        binding.tvScore.text = "Score: ${gameManager.score}"
        binding.tvMoney.text = "RM ${gameManager.money}"
        binding.tvSavings.text = "RM ${gameManager.savings}"
        binding.tvExpenses.text = "RM ${gameManager.expenses}"

        // Turn progress bar: advances as questions/decisions are answered, 100% filled on Turn 15
        binding.pbBudget.max = gameManager.maxTurns
        val anim = android.animation.ObjectAnimator.ofInt(
            binding.pbBudget,
            "progress",
            binding.pbBudget.progress,
            currentTurn
        )
        anim.duration = 400L
        anim.interpolator = android.view.animation.DecelerateInterpolator()
        anim.start()
    }

    private fun updateTileInfoDisplay(tile: Tile) {
        binding.tvTileName.text = tile.name
        binding.tvTileDesc.text = tile.description
        binding.ivTileIcon.setImageResource(tile.iconRes)
        binding.ivTileIcon.setColorFilter(ContextCompat.getColor(this, tile.categoryColorRes))
    }

    private fun rollDiceAndMove() {
        isRolling = true
        binding.btnRollDice.isEnabled = false
        SoundManager.playDiceRollSound()

        val diceDrawables = intArrayOf(
            R.drawable.ic_dice_1,
            R.drawable.ic_dice_2,
            R.drawable.ic_dice_3,
            R.drawable.ic_dice_4,
            R.drawable.ic_dice_5,
            R.drawable.ic_dice_6
        )

        var rollTicks = 0
        val rollRunnable = object : Runnable {
            override fun run() {
                if (rollTicks < 8) {
                    val randomDice = diceDrawables.random()
                    binding.ivDice.setImageResource(randomDice)
                    rollTicks++
                    handler.postDelayed(this, 60)
                } else {
                    val finalDiceValue = gameManager.rollDice()
                    binding.ivDice.setImageResource(diceDrawables[finalDiceValue - 1])

                    val (landedTile, passedStart) = gameManager.movePlayer(finalDiceValue)
                    binding.boardView.setPlayerPosition(gameManager.currentPosition, true)
                    updateTileInfoDisplay(landedTile)
                    updateHUD()

                    if (passedStart) {
                        SoundManager.playCoinSound()
                        Toast.makeText(this@GameActivity, "START passed! Collect RM 500 bonus!", Toast.LENGTH_LONG).show()
                    }

                    handler.postDelayed({
                        handleTileEvent(landedTile)
                    }, 500)
                }
            }
        }
        handler.post(rollRunnable)
    }

    private fun handleTileEvent(tile: Tile) {
        when (tile.type) {
            TileType.START -> {
                SoundManager.playCoinSound()
                showFeedbackDialog("START Bonus", "Collect RM 500 bonus as you pass START! Keep managing your budget wisely.")
            }
            TileType.QUIZ -> {
                val scenario = QuizRepository.getScenarioForTile(tile.id)
                showQuizDialog(scenario)
            }
            else -> {
                showCategoryChoiceDialog(tile)
            }
        }
    }

    private fun showQuizDialog(scenario: QuizScenario) {
        val dialogBinding = DialogQuizChoiceBinding.inflate(layoutInflater)

        dialogBinding.tvQuizCategory.text = scenario.category
        dialogBinding.tvQuizTitle.text = scenario.title
        dialogBinding.tvQuizDesc.text = scenario.description

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        // Randomly shuffle choice options so button positions vary every time!
        val shuffledChoices = scenario.choices.shuffled()

        if (shuffledChoices.isNotEmpty()) {
            val c1 = shuffledChoices[0]
            dialogBinding.btnChoice1.visibility = android.view.View.VISIBLE
            dialogBinding.btnChoice1.text = c1.text
            dialogBinding.btnChoice1.setOnClickListener {
                gameManager.applyQuizChoice(c1)
                dialog.dismiss()
                showChoiceFeedback(c1.feedback, c1.moneyDelta, c1.savingsDelta, c1.scoreDelta)
            }
        }

        if (shuffledChoices.size >= 2) {
            val c2 = shuffledChoices[1]
            dialogBinding.btnChoice2.visibility = android.view.View.VISIBLE
            dialogBinding.btnChoice2.text = c2.text
            dialogBinding.btnChoice2.setOnClickListener {
                gameManager.applyQuizChoice(c2)
                dialog.dismiss()
                showChoiceFeedback(c2.feedback, c2.moneyDelta, c2.savingsDelta, c2.scoreDelta)
            }
        } else {
            dialogBinding.btnChoice2.visibility = android.view.View.GONE
        }

        if (shuffledChoices.size >= 3) {
            val c3 = shuffledChoices[2]
            dialogBinding.btnChoice3.visibility = android.view.View.VISIBLE
            dialogBinding.btnChoice3.text = c3.text
            dialogBinding.btnChoice3.setOnClickListener {
                gameManager.applyQuizChoice(c3)
                dialog.dismiss()
                showChoiceFeedback(c3.feedback, c3.moneyDelta, c3.savingsDelta, c3.scoreDelta)
            }
        } else {
            dialogBinding.btnChoice3.visibility = android.view.View.GONE
        }

        dialog.show()
    }

    private data class CatOptionData(
        val text: String,
        val amountSpent: Int,
        val amountSaved: Int,
        val choiceType: GameManager.ChoiceType,
        val feedback: String,
        val scoreDelta: Int
    )

    private fun showCategoryChoiceDialog(tile: Tile) {
        val dialogBinding = DialogCategoryChoiceBinding.inflate(layoutInflater)

        dialogBinding.tvCategoryTitle.text = tile.name
        dialogBinding.tvCategoryDesc.text = tile.description

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        val optionsList = when (tile.type) {
            TileType.FOOD -> listOf(
                CatOptionData("Cook Groceries (Pay RM 150)", 150, 0, GameManager.ChoiceType.WISE_BUDGET, "Wise choice! Cooking groceries kept your food spending within budget.", 20),
                CatOptionData("Order Fancy Delivery / Dine Out (Pay RM 450)", 450, 0, GameManager.ChoiceType.EXCEEDING_BUDGET, "Exceeding budget! Frequent dining out drained your wallet.", -20),
                CatOptionData("Meal Prep & Deposit RM 100 into Savings", 100, 100, GameManager.ChoiceType.SAVING, "Awesome saving habit! Meal prep saved RM 100 for your emergency fund.", 10)
            )
            TileType.TRANSPORT -> listOf(
                CatOptionData("Public Transport Pass (Pay RM 80)", 80, 0, GameManager.ChoiceType.WISE_BUDGET, "Great decision! Public transport is economical and reliable.", 20),
                CatOptionData("Premium Taxi Rides Daily (Pay RM 300)", 300, 0, GameManager.ChoiceType.EXCEEDING_BUDGET, "Exceeding budget! Premium rides cost way too much.", -20),
                CatOptionData("Walk / Cycling & Save RM 80", 0, 80, GameManager.ChoiceType.SAVING, "Healthy & Frugal! You saved RM 80 on transport costs.", 10)
            )
            TileType.RENT -> listOf(
                CatOptionData("Pay Standard Monthly Rent (Pay RM 600)", 600, 0, GameManager.ChoiceType.WISE_BUDGET, "Responsible housing payment! Rent settled on time.", 20),
                CatOptionData("Upgrade to Luxury Suite (Pay RM 1,200)", 1200, 0, GameManager.ChoiceType.EXCEEDING_BUDGET, "Over budget! Housing cost ate up nearly half your salary.", -20)
            )
            TileType.BILLS -> listOf(
                CatOptionData("Pay Electricity & Water Bills (Pay RM 180)", 180, 0, GameManager.ChoiceType.WISE_BUDGET, "Utilities paid on time! No late fees incurred.", 20),
                CatOptionData("Leave AC Running 24/7 (Pay RM 380)", 380, 0, GameManager.ChoiceType.UNNECESSARY, "Unnecessary spending! Excessive AC usage raised electricity bills.", -10)
            )
            TileType.EDUCATION -> listOf(
                CatOptionData("Buy Career Certification Course (Pay RM 150)", 150, 0, GameManager.ChoiceType.WISE_BUDGET, "Excellent investment! Upskilling boosts your career income.", 20),
                CatOptionData("Skip Learning & Spend on Gaming (Pay RM 200)", 200, 0, GameManager.ChoiceType.UNNECESSARY, "Unnecessary spending on entertainment instead of growth.", -10),
                CatOptionData("Use Free Open Source Materials & Save RM 150", 0, 150, GameManager.ChoiceType.SAVING, "Smart resourceful learning! Saved RM 150.", 10)
            )
            TileType.SAVINGS -> listOf(
                CatOptionData("Deposit RM 300 into Emergency Savings", 0, 300, GameManager.ChoiceType.SAVING, "Fantastic! Deposit of RM 300 grew your savings buffer.", 10),
                CatOptionData("Skip Saving & Spend RM 300 on Shoes", 300, 0, GameManager.ChoiceType.UNNECESSARY, "Unnecessary impulse shopping! You skipped saving money.", -10)
            )
            TileType.ENTERTAINMENT -> listOf(
                CatOptionData("Discounted Cinema & Snacks (Pay RM 50)", 50, 0, GameManager.ChoiceType.WISE_BUDGET, "Balanced fun! Enjoyed leisure within your target budget.", 20),
                CatOptionData("VIP Lounge & Party Night (Pay RM 400)", 400, 0, GameManager.ChoiceType.EXCEEDING_BUDGET, "Exceeding budget! Luxury nightlife drained your money.", -20),
                CatOptionData("Home Movie Night & Put RM 50 into Savings", 0, 50, GameManager.ChoiceType.SAVING, "Great self control! Cozy home entertainment saved money.", 10)
            )
            else -> listOf(
                CatOptionData("Pay Base Expense (Pay RM 100)", 100, 0, GameManager.ChoiceType.WISE_BUDGET, "Paid baseline expense within budget.", 20)
            )
        }

        // Shuffle category answer choices randomly every time!
        val shuffled = optionsList.shuffled()

        if (shuffled.isNotEmpty()) {
            val o1 = shuffled[0]
            dialogBinding.btnOptionWise.visibility = android.view.View.VISIBLE
            dialogBinding.btnOptionWise.text = o1.text
            dialogBinding.btnOptionWise.setOnClickListener {
                gameManager.applyCategoryDecision(tile.type, o1.amountSpent, o1.amountSaved, o1.choiceType)
                dialog.dismiss()
                showChoiceFeedback(o1.feedback, if (o1.amountSpent > 0) -o1.amountSpent else 0, o1.amountSaved, o1.scoreDelta)
            }
        } else {
            dialogBinding.btnOptionWise.visibility = android.view.View.GONE
        }

        if (shuffled.size >= 2) {
            val o2 = shuffled[1]
            dialogBinding.btnOptionImpulsive.visibility = android.view.View.VISIBLE
            dialogBinding.btnOptionImpulsive.text = o2.text
            dialogBinding.btnOptionImpulsive.setOnClickListener {
                gameManager.applyCategoryDecision(tile.type, o2.amountSpent, o2.amountSaved, o2.choiceType)
                dialog.dismiss()
                showChoiceFeedback(o2.feedback, if (o2.amountSpent > 0) -o2.amountSpent else 0, o2.amountSaved, o2.scoreDelta)
            }
        } else {
            dialogBinding.btnOptionImpulsive.visibility = android.view.View.GONE
        }

        if (shuffled.size >= 3) {
            val o3 = shuffled[2]
            dialogBinding.btnOptionSave.visibility = android.view.View.VISIBLE
            dialogBinding.btnOptionSave.text = o3.text
            dialogBinding.btnOptionSave.setOnClickListener {
                gameManager.applyCategoryDecision(tile.type, o3.amountSpent, o3.amountSaved, o3.choiceType)
                dialog.dismiss()
                showChoiceFeedback(o3.feedback, if (o3.amountSpent > 0) -o3.amountSpent else 0, o3.amountSaved, o3.scoreDelta)
            }
        } else {
            dialogBinding.btnOptionSave.visibility = android.view.View.GONE
        }

        dialog.show()
    }

    private fun showChoiceFeedback(feedbackText: String, moneyDelta: Int, savingsDelta: Int, scoreDelta: Int) {
        if (scoreDelta > 0 || savingsDelta > 0 || moneyDelta > 0) {
            SoundManager.playCoinSound()
        } else if (scoreDelta < 0 || moneyDelta < 0) {
            SoundManager.playExpenseSound()
        }

        updateHUD()

        val moneyImpact = when {
            moneyDelta > 0 -> "+RM $moneyDelta Cash"
            moneyDelta < 0 -> "-RM ${-moneyDelta} Cash"
            else -> ""
        }
        val savingsImpact = when {
            savingsDelta > 0 -> "+RM $savingsDelta Savings"
            savingsDelta < 0 -> "-RM ${-savingsDelta} Savings"
            else -> ""
        }
        val scoreImpact = when {
            scoreDelta > 0 -> "+$scoreDelta Points"
            scoreDelta < 0 -> "$scoreDelta Points"
            else -> ""
        }

        val fullMsg = "$feedbackText\n\nImpact:\n$moneyImpact $savingsImpact $scoreImpact".trim()

        AlertDialog.Builder(this)
            .setTitle("Financial Result")
            .setMessage(fullMsg)
            .setPositiveButton("Continue") { d, _ ->
                d.dismiss()
                checkGameOverOrNextTurn()
            }
            .setCancelable(false)
            .show()
    }

    private fun showFeedbackDialog(title: String, message: String) {
        updateHUD()
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Continue") { d, _ ->
                d.dismiss()
                checkGameOverOrNextTurn()
            }
            .setCancelable(false)
            .show()
    }

    private fun checkGameOverOrNextTurn() {
        gameManager.advanceTurn()
        updateHUD()
        if (gameManager.isGameOver()) {
            val intent = Intent(this, GameResultActivity::class.java).apply {
                putExtra("finalMoney", gameManager.money)
                putExtra("baseSavings", gameManager.savings)
                putExtra("savingsBonusPercent", gameManager.getSavingsBonusPercent())
                putExtra("totalSavings", gameManager.calculateFinalSavings())
                putExtra("totalExpenses", gameManager.expenses)
                putExtra("finalScore", gameManager.score)
                putExtra("performanceRank", gameManager.getPerformanceRank())
            }
            startActivity(intent)
            finish()
        } else {
            isRolling = false
            binding.btnRollDice.isEnabled = true
        }
    }
}