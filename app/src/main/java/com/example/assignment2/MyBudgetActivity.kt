package com.example.assignment2

import android.os.Bundle
import com.example.assignment2.databinding.ActivityMyBudgetBinding

class MyBudgetActivity : BaseActivity() {

    private lateinit var binding: ActivityMyBudgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}