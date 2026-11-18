package com.example.assignment2

import android.content.Intent
import android.os.Bundle
import com.example.assignment2.data.ScoreRepository
import com.example.assignment2.databinding.ActivityMainMenuBinding
import com.example.assignment2.sound.SoundManager

class MainMenuActivity : BaseActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SoundManager.initSettings(this)
        updateHighScoreDisplay()

        binding.btnStartGame.setOnClickListener {
            SoundManager.playClickSound()
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.btnHowToPlay.setOnClickListener {
            SoundManager.playClickSound()
            startActivity(Intent(this, HowToPlayActivity::class.java))
        }

        binding.btnMyBudget.setOnClickListener {
            SoundManager.playClickSound()
            startActivity(Intent(this, MyBudgetActivity::class.java))
        }

        binding.btnMyScore.setOnClickListener {
            SoundManager.playClickSound()
            startActivity(Intent(this, MyScoreActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            SoundManager.playClickSound()
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnExit.setOnClickListener {
            SoundManager.playClickSound()
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        updateHighScoreDisplay()
        SoundManager.startBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        SoundManager.stopBackgroundMusic()
    }

    private fun updateHighScoreDisplay() {
        val highScore = ScoreRepository.getHighScore(this)
        binding.tvHighScore.text = "High Score: $highScore pts"
    }
}