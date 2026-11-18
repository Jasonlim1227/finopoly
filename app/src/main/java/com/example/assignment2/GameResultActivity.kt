package com.example.assignment2

import android.content.Intent
import android.os.Bundle
import com.example.assignment2.data.ScoreRepository
import com.example.assignment2.databinding.ActivityGameResultBinding
import com.example.assignment2.model.GameResultData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameResultActivity : BaseActivity() {

    private lateinit var binding: ActivityGameResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        com.example.assignment2.sound.SoundManager.playWinFanfare()

        val finalMoney = intent.getIntExtra("finalMoney", 0)
        val baseSavings = intent.getIntExtra("baseSavings", 0)
        val bonusPercent = intent.getIntExtra("savingsBonusPercent", 0)
        val finalTotalSavings = intent.getIntExtra("totalSavings", baseSavings + (baseSavings * bonusPercent / 100))
        val totalExpenses = intent.getIntExtra("totalExpenses", 0)
        val finalScore = intent.getIntExtra("finalScore", 0)
        val performanceRank = intent.getStringExtra("performanceRank") ?: "Budgeter"

        binding.tvRemainingMoney.text = "RM $finalMoney"
        binding.tvTotalSavings.text = "RM $finalTotalSavings"
        binding.tvTotalExpenses.text = "RM $totalExpenses"
        binding.tvFinalScore.text = "Final Score: $finalScore pts"
        binding.tvPerformanceRank.text = performanceRank

        if (bonusPercent > 0) {
            binding.tvSavingsBonusDetail.text = "Base RM $baseSavings (+$bonusPercent% Score Bonus)"
        } else {
            binding.tvSavingsBonusDetail.text = "Base RM $baseSavings (+0% Score Bonus)"
        }

        // Save result
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date())

        val resultData = GameResultData(
            date = dateStr,
            finalMoney = finalMoney,
            totalSavings = finalTotalSavings,
            totalExpenses = totalExpenses,
            finalScore = finalScore,
            performanceRank = performanceRank
        )

        ScoreRepository.saveGameResult(this, resultData)

        binding.btnPlayAgain.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnMyScore.setOnClickListener {
            val intent = Intent(this, MyScoreActivity::class.java)
            startActivity(intent)
        }

        binding.btnMainMenu.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}