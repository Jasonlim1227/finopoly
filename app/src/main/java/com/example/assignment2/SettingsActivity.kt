package com.example.assignment2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.assignment2.databinding.ActivitySettingsBinding
import com.example.assignment2.sound.SoundManager

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SoundManager.initSettings(this)

        binding.switchBgm.isChecked = SoundManager.isBgmEnabled
        binding.switchSfx.isChecked = SoundManager.isSfxEnabled

        binding.switchBgm.setOnCheckedChangeListener { _, isChecked ->
            SoundManager.setBgmEnabled(this, isChecked)
        }

        binding.switchSfx.setOnCheckedChangeListener { _, isChecked ->
            SoundManager.setSfxEnabled(this, isChecked)
        }

        // BGM Volume Slider
        binding.seekBarBgmVolume.progress = SoundManager.bgmVolume
        binding.tvBgmVolumeVal.text = "${SoundManager.bgmVolume}%"
        binding.seekBarBgmVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvBgmVolumeVal.text = "$progress%"
                if (fromUser) {
                    SoundManager.setBgmVolume(this@SettingsActivity, progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // SFX Volume Slider
        binding.seekBarSfxVolume.progress = SoundManager.sfxVolume
        binding.tvSfxVolumeVal.text = "${SoundManager.sfxVolume}%"
        binding.seekBarSfxVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSfxVolumeVal.text = "$progress%"
                if (fromUser) {
                    SoundManager.setSfxVolume(this@SettingsActivity, progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    SoundManager.playClickSound()
                }
            }
        })

        binding.btnResetScores.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reset Score History")
                .setMessage("Are you sure you want to delete all past game scores and history?")
                .setPositiveButton("Reset") { d, _ ->
                    getSharedPreferences("finopoly_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                    Toast.makeText(this, "Score history reset successfully!", Toast.LENGTH_SHORT).show()
                    d.dismiss()
                }
                .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                .show()
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out of the game?")
                .setPositiveButton("Logout") { d, _ ->
                    d.dismiss()
                    SoundManager.stopBackgroundMusic()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                .show()
        }

        binding.btnBack.setOnClickListener {
            finish()
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
}