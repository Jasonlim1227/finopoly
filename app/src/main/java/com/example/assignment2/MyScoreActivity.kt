package com.example.assignment2

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment2.data.ScoreRepository
import com.example.assignment2.databinding.ActivityMyScoreBinding
import com.example.assignment2.ui.ScoreAdapter

class MyScoreActivity : BaseActivity() {

    private lateinit var binding: ActivityMyScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val history = ScoreRepository.getGameHistory(this)
        val highScore = ScoreRepository.getHighScore(this)

        binding.tvHighScoreBig.text = "$highScore pts"

        if (history.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
            binding.rvHistory.layoutManager = LinearLayoutManager(this)
            binding.rvHistory.adapter = ScoreAdapter(history)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}