package com.example.assignment2

import android.os.Bundle
import com.example.assignment2.databinding.ActivityHowToPlayBinding

class HowToPlayActivity : BaseActivity() {

    private lateinit var binding: ActivityHowToPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}