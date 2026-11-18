package com.example.assignment2.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import com.example.assignment2.R
import kotlin.math.sin

object SoundManager {

    private const val PREF_SOUND = "finopoly_sound_prefs"
    private const val KEY_BGM = "bgm_enabled"
    private const val KEY_SFX = "sfx_enabled"
    private const val KEY_BGM_VOL = "bgm_volume"
    private const val KEY_SFX_VOL = "sfx_volume"

    var isBgmEnabled: Boolean = true
        private set
    var isSfxEnabled: Boolean = true
        private set
    var isFastSpeed: Boolean = false
        private set

    var bgmVolume: Int = 50 // 0 to 100
        private set
    var sfxVolume: Int = 80 // 0 to 100
        private set

    private var isBgPlaying = false
    private var bgThread: Thread? = null
    private var mediaPlayer: MediaPlayer? = null

    fun initSettings(context: Context) {
        val prefs = context.getSharedPreferences(PREF_SOUND, Context.MODE_PRIVATE)
        isBgmEnabled = prefs.getBoolean(KEY_BGM, true)
        isSfxEnabled = prefs.getBoolean(KEY_SFX, true)
        bgmVolume = prefs.getInt(KEY_BGM_VOL, 50)
        sfxVolume = prefs.getInt(KEY_SFX_VOL, 80)
    }

    fun setBgmEnabled(context: Context, enabled: Boolean) {
        isBgmEnabled = enabled
        context.getSharedPreferences(PREF_SOUND, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_BGM, enabled).apply()

        if (enabled) {
            startBackgroundMusic(context)
        } else {
            stopBackgroundMusic()
        }
    }

    fun setSfxEnabled(context: Context, enabled: Boolean) {
        isSfxEnabled = enabled
        context.getSharedPreferences(PREF_SOUND, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SFX, enabled).apply()
    }

    fun setBgmVolume(context: Context, volume: Int) {
        bgmVolume = volume.coerceIn(0, 100)
        context.getSharedPreferences(PREF_SOUND, Context.MODE_PRIVATE)
            .edit().putInt(KEY_BGM_VOL, bgmVolume).apply()

        val vFactor = bgmVolume / 100.0f
        try {
            mediaPlayer?.setVolume(vFactor, vFactor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSfxVolume(context: Context, volume: Int) {
        sfxVolume = volume.coerceIn(0, 100)
        context.getSharedPreferences(PREF_SOUND, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SFX_VOL, sfxVolume).apply()
    }

    // Professional Crisp Tone Generator
    private fun generateCrispTone(
        freq: Float,
        durationMs: Int,
        volume: Float = 0.3f
    ): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val generatedSnd = ByteArray(2 * numSamples)

        var idx = 0
        var phase = 0.0
        for (i in 0 until numSamples) {
            phase += 2.0 * Math.PI * freq / sampleRate
            val sampleVal = sin(phase) + 0.25 * sin(phase * 2.0)

            // Smooth envelope
            val envelope = if (i > numSamples - 500) (numSamples - i) / 500.0 else 1.0
            val valShort = (sampleVal * 22000.0 * volume * envelope).toInt().coerceIn(-32768, 32767).toShort()

            generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = (valShort.toInt() shr 8 and 0x00ff).toByte()
        }
        return generatedSnd
    }

    // Serious Jazz Trumpet & Stride Rhythm Audio Synthesis Engine
    private fun generateJazzTrumpetAudioBuffer(
        bassFreq: Float,
        chordFreqs: FloatArray,
        trumpetLeadFreq: Float,
        durationMs: Int,
        volume: Float = 0.09f
    ): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val buffer = ByteArray(2 * numSamples)

        var idx = 0
        var phaseBass = 0.0
        val phaseChords = DoubleArray(chordFreqs.size)
        var phaseTrumpet = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate

            // 1. Double Bass / Stride Bass String
            phaseBass += 2.0 * Math.PI * bassFreq / sampleRate
            val bassVal = sin(phaseBass) + 0.3 * sin(phaseBass * 2.0)

            // 2. Vintage Jazz Piano Accompaniment Chords
            var chordVal = 0.0
            for (c in chordFreqs.indices) {
                phaseChords[c] += 2.0 * Math.PI * chordFreqs[c] / sampleRate
                chordVal += sin(phaseChords[c]) + 0.2 * sin(phaseChords[c] * 2.0)
            }
            if (chordFreqs.isNotEmpty()) chordVal /= chordFreqs.size

            // 3. Elegant Jazz Trumpet Voice (Brassy Harmonics + Subtle 4.8Hz Vibrato)
            var trumpetVal = 0.0
            if (trumpetLeadFreq > 0f) {
                val vibrato = (sin(2.0 * Math.PI * 4.8 * t) * (trumpetLeadFreq * 0.012)).toFloat()
                val currentTrumpetFreq = trumpetLeadFreq + vibrato

                phaseTrumpet += 2.0 * Math.PI * currentTrumpetFreq / sampleRate

                // Brassy Jazz Trumpet Spectrum: Fundamental + Strong Brassy Harmonics
                trumpetVal = 0.45 * sin(phaseTrumpet) +
                             0.35 * sin(phaseTrumpet * 2.0) +
                             0.25 * sin(phaseTrumpet * 3.0) +
                             0.18 * sin(phaseTrumpet * 4.0) +
                             0.10 * sin(phaseTrumpet * 5.0) +
                             0.05 * sin(phaseTrumpet * 6.0)
            }

            // Composite Mix
            val mixedVal = (bassVal * 0.40) + (chordVal * 0.30) + (trumpetVal * 0.48)

            // Professional Smooth Envelope
            val envelope = if (i > numSamples - 800) (numSamples - i) / 800.0 else 1.0
            val finalSample = (mixedVal * 22000.0 * volume * envelope).toInt().coerceIn(-32768, 32767).toShort()

            buffer[idx++] = (finalSample.toInt() and 0x00ff).toByte()
            buffer[idx++] = (finalSample.toInt() shr 8 and 0x00ff).toByte()
        }
        return buffer
    }

    private fun playTone(freq: Float, durationMs: Int, volume: Float = 0.3f, isSfx: Boolean = true) {
        if (isSfx && (!isSfxEnabled || sfxVolume <= 0)) return
        if (!isSfx && (!isBgmEnabled || bgmVolume <= 0)) return

        val sfxFactor = sfxVolume / 100.0f
        val effectiveVolume = volume * sfxFactor

        Thread {
            try {
                val soundData = generateCrispTone(freq, durationMs, effectiveVolume)
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(44100)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(soundData.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(soundData, 0, soundData.size)
                track.play()
                Thread.sleep(durationMs.toLong())
                track.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun playClickSound() {
        // Professional crisp button click
        playTone(800f, 35, 0.22f, isSfx = true)
    }

    fun playDiceRollSound() {
        if (!isSfxEnabled || sfxVolume <= 0) return
        Thread {
            try {
                // Crisp, realistic wooden dice cup roll clicks
                val rollFreqs = floatArrayOf(450f, 520f, 480f, 550f, 500f, 580f, 520f)
                for (f in rollFreqs) {
                    playTone(f, 30, 0.28f, isSfx = true)
                    Thread.sleep(50)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun playPawnStepSound() {
        // Warm, clean stage step tap
        playTone(520f, 50, 0.25f, isSfx = true)
    }

    fun playCoinSound() {
        if (!isSfxEnabled || sfxVolume <= 0) return
        Thread {
            try {
                // Elegant brass double chime
                playTone(1046.50f, 70, 0.35f, isSfx = true) // C6
                Thread.sleep(70)
                playTone(1318.51f, 120, 0.40f, isSfx = true) // E6
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun playExpenseSound() {
        if (!isSfxEnabled || sfxVolume <= 0) return
        Thread {
            try {
                // Serious, subtle low brass note
                playTone(293.66f, 90, 0.35f, isSfx = true) // D3
                Thread.sleep(90)
                playTone(261.63f, 130, 0.38f, isSfx = true) // C3
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun playWinFanfare() {
        if (!isSfxEnabled || sfxVolume <= 0) return
        Thread {
            try {
                // Elegant classic brass fanfare
                val fanfareNotes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f)
                for (f in fanfareNotes) {
                    playTone(f, 110, 0.40f, isSfx = true)
                    Thread.sleep(100)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun startBackgroundMusic(context: Context? = null) {
        if (!isBgmEnabled) return

        val vFactor = bgmVolume / 100.0f

        // 1. If MediaPlayer is already playing bg_music.mp3 across pages, update volume & keep playing!
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.setVolume(vFactor, vFactor)
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isBgPlaying = true
            return
        }

        isBgPlaying = true

        // 2. Play bg_music.mp3 from res/raw/
        if (context != null) {
            try {
                mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.bg_music).apply {
                    isLooping = true
                    setVolume(vFactor, vFactor)
                    start()
                }
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 3. Fallback synthesis if raw resource unavailable
        bgThread = Thread {
            data class JazzTrumpetBeat(
                val bass: Float,
                val chord: FloatArray,
                val trumpetLead: Float,
                val durationMs: Int = 420
            )

            val trumpetArrangement = listOf(
                // Cmaj7 - Jazz Trumpet Solo Line
                JazzTrumpetBeat(130.81f, floatArrayOf(261.63f, 329.63f, 392.00f), 523.25f),  // C5 Trumpet
                JazzTrumpetBeat(164.81f, floatArrayOf(261.63f, 329.63f, 392.00f), 659.25f),  // E5 Trumpet
                JazzTrumpetBeat(174.61f, floatArrayOf(261.63f, 329.63f, 392.00f), 783.99f),  // G5 Trumpet
                JazzTrumpetBeat(185.00f, floatArrayOf(261.63f, 329.63f, 392.00f), 880.00f),  // A5 Trumpet

                // A7alt - Jazz Trumpet Phrase
                JazzTrumpetBeat(220.00f, floatArrayOf(220.00f, 277.18f, 329.63f), 987.77f),  // B5 Trumpet
                JazzTrumpetBeat(207.65f, floatArrayOf(220.00f, 277.18f, 329.63f), 1046.50f), // C6 Trumpet
                JazzTrumpetBeat(196.00f, floatArrayOf(220.00f, 277.18f, 329.63f), 987.77f),  // B5 Trumpet
                JazzTrumpetBeat(185.00f, floatArrayOf(220.00f, 277.18f, 329.63f), 880.00f),  // A5 Trumpet

                // Dm7 - Serious Jazz Trumpet Line
                JazzTrumpetBeat(146.83f, floatArrayOf(293.66f, 349.23f, 440.00f), 698.46f),  // F5 Trumpet
                JazzTrumpetBeat(174.61f, floatArrayOf(293.66f, 349.23f, 440.00f), 783.99f),  // G5 Trumpet
                JazzTrumpetBeat(196.00f, floatArrayOf(293.66f, 349.23f, 440.00f), 880.00f),  // A5 Trumpet
                JazzTrumpetBeat(220.00f, floatArrayOf(293.66f, 349.23f, 440.00f), 1046.50f), // C6 Trumpet

                // G7 - Crisp Trumpet Resolution
                JazzTrumpetBeat(196.00f, floatArrayOf(196.00f, 246.94f, 293.66f), 1174.66f), // D6 Trumpet
                JazzTrumpetBeat(174.61f, floatArrayOf(196.00f, 246.94f, 293.66f), 1046.50f), // C6 Trumpet
                JazzTrumpetBeat(164.81f, floatArrayOf(196.00f, 246.94f, 293.66f), 880.00f),  // A5 Trumpet
                JazzTrumpetBeat(146.83f, floatArrayOf(196.00f, 246.94f, 293.66f), 659.25f)   // E5 Trumpet
            )

            try {
                while (isBgPlaying && isBgmEnabled) {
                    for (beat in trumpetArrangement) {
                        if (!isBgPlaying || !isBgmEnabled) break
                        val soundBuffer = generateJazzTrumpetAudioBuffer(beat.bass, beat.chord, beat.trumpetLead, beat.durationMs, vFactor * 0.15f)
                        val track = AudioTrack.Builder()
                            .setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_GAME)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build()
                            )
                            .setAudioFormat(
                                AudioFormat.Builder()
                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(44100)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                    .build()
                            )
                            .setBufferSizeInBytes(soundBuffer.size)
                            .setTransferMode(AudioTrack.MODE_STATIC)
                            .build()

                        track.write(soundBuffer, 0, soundBuffer.size)
                        track.play()
                        Thread.sleep(beat.durationMs.toLong())
                        track.release()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.apply { start() }
    }

    fun stopBackgroundMusic() {
        isBgPlaying = false
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        bgThread?.interrupt()
        bgThread = null
    }

    fun toggleBgm(context: Context): Boolean {
        val newState = !isBgmEnabled
        setBgmEnabled(context, newState)
        return newState
    }
}