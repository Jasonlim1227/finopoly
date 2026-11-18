package com.example.assignment2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.assignment2.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString()?.trim() ?: ""

            if (username == "player" && password == "money123") {
                val intent = Intent(this, MainMenuActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.invalid_login), Toast.LENGTH_SHORT).show()
            }
        }
    }
}