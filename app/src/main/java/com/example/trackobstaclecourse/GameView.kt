package com.example.trackobstaclecourse

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.media.AudioAttributes
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackobstaclecourse.MainActivity.Companion.currentTheme

class GameActivity : AppCompatActivity(), GameView.GameListener {

    private lateinit var gameView: GameView
    private lateinit var scoreText: TextView
    private lateinit var livesText: TextView
    private lateinit var difficultyProgress: ProgressBar
    private lateinit var pauseButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var vibrator: Vibrator

    private var isPaused = false
    private var score = 0
    private var startingLives = 3

    private var activeTheme = MainActivity.currentTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MainActivity.currentTheme)

        Log.w("GameActivity", "OnCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TrackRunnerPrefs", MODE_PRIVATE)
        startingLives = sharedPreferences.getInt("starting_lives", 3)

        // Initialize Vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        // Initialize views
        val gameContainer = findViewById<FrameLayout>(R.id.gameViewContainer)
        scoreText = findViewById(R.id.scoreText)
        livesText = findViewById(R.id.livesText)
        difficultyProgress = findViewById(R.id.difficultyProgress)
        pauseButton = findViewById(R.id.pauseButton)
        val leftButton = findViewById<Button>(R.id.leftButton)
        val rightButton = findViewById<Button>(R.id.rightButton)

        // Create and add GameView
        gameView = GameView(this)
        gameView.gameListener = this
        gameView.setLives(startingLives)
        gameContainer.addView(gameView)

        // Set up controls
        leftButton.setOnClickListener {
            gameView.movePlayerLeft()
        }

        rightButton.setOnClickListener {
            gameView.movePlayerRight()
        }

        pauseButton.setOnClickListener {
            togglePause()
        }

        // Update initial UI
        updateScoreDisplay()
        updateLivesDisplay(startingLives)
    }

    private fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            gameView.pauseGame()
            pauseButton.text = getString(R.string.resume)
        } else {
            gameView.resumeGame()
            pauseButton.text = getString(R.string.pause)
        }
    }

    override fun onScoreUpdate(newScore: Int) {
        score = newScore
        runOnUiThread {
            updateScoreDisplay()
            // Update difficulty progress (max at 100)
            difficultyProgress.progress = minOf(newScore / 10, 100)
        }
    }

    override fun onLivesUpdate(lives: Int) {
        runOnUiThread {
            updateLivesDisplay(lives)
        }
    }

    override fun onCollision() {
        // Vibrate on collision
        Log.w("GameActivity", "OnCollision")

        val vibrationEnabled = sharedPreferences.getBoolean("vibration_enabled", true)
        if (vibrationEnabled) {
            Log.w("GameActivity", "OnCollision - first if")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.w("GameActivity", "OnCollision - second if")
                val vibe = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                val audioAttr = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()
                vibrator.vibrate(vibe, audioAttr)
            } else {
                Log.w("GameActivity", "OnCollision - else")
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }
        }
    }

    override fun onGameOver(finalScore: Int) {
        runOnUiThread {
            // Save high score if needed
            val currentHighScore = sharedPreferences.getInt("high_score", 0)
            val isNewHighScore = finalScore > currentHighScore
            if (isNewHighScore) {
                sharedPreferences.edit().putInt("high_score", finalScore).apply()
            }

            // Navigate to Game Over screen
            val intent = Intent(this, GameOverActivity::class.java)
            intent.putExtra("final_score", finalScore)
            intent.putExtra("is_new_high_score", isNewHighScore)
            startActivity(intent)
            finish()
        }
    }

    private fun updateScoreDisplay() {
        scoreText.text = "SCORE: $score"
    }

    private fun updateLivesDisplay(lives: Int) {
        // Display hearts for lives
        val hearts = "❤️".repeat(lives)
        livesText.text = hearts
    }

    override fun onPause() {
        super.onPause()
        if (!isPaused) {
            gameView.pauseGame()
        }
    }

    override fun onResume() {
        super.onResume()
        // Don't auto-resume, let player decide
        if (activeTheme != currentTheme) {
            // If they are different, it means the user changed settings!
            recreate() // Restart this screen to apply the new colors
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.stopGame()
    }
}

