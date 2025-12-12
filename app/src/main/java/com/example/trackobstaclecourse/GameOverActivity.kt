package com.example.trackobstaclecourse

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackobstaclecourse.MainActivity.Companion.currentTheme
import com.google.firebase.database.FirebaseDatabase

class GameOverActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var finalScore = 0
    private var isNewHighScore = false

    private var activeTheme = MainActivity.currentTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MainActivity.currentTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameOverContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("TrackRunnerPrefs", MODE_PRIVATE)

        // Get score from intent
        finalScore = intent.getIntExtra("final_score", 0)
        isNewHighScore = intent.getBooleanExtra("is_new_high_score", false)

        // Initialize views
        val gameOverText = findViewById<TextView>(R.id.gameOverText)
        val finalScoreText = findViewById<TextView>(R.id.finalScoreText)
        val newHighScoreText = findViewById<TextView>(R.id.newHighScoreText)
        val ratingBar = findViewById<RatingBar>(R.id.gameRatingBar)
        val ratingLabel = findViewById<TextView>(R.id.ratingLabel)
        val playerNameInput = findViewById<EditText>(R.id.playerNameInput)
        val submitScoreButton = findViewById<Button>(R.id.submitScoreButton)
        val playAgainButton = findViewById<Button>(R.id.playAgainButton)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)

        // Display score
        finalScoreText.text = getString(R.string.final_score, finalScore)

        // Show high score badge if applicable
        if (isNewHighScore) {
            newHighScoreText.visibility = View.VISIBLE
        }

        // Load saved player name
        val savedName = sharedPreferences.getString("player_name", "")
        playerNameInput.setText(savedName)

        // Rating bar listener (New GUI Component #2)
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            val feedback = when {
                rating <= 1 -> "Thanks for the feedback!"
                rating <= 2 -> "We'll try to improve!"
                rating <= 3 -> "Not bad!"
                rating <= 4 -> "Great!"
                else -> "Awesome! 🎉"
            }
            ratingLabel.text = feedback
        }

        // Submit score to Firebase
        submitScoreButton.setOnClickListener {
            val playerName = playerNameInput.text.toString().trim()
            if (playerName.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save player name locally
            sharedPreferences.edit().putString("player_name", playerName).apply()

            // Submit to Firebase
            submitScoreToFirebase(playerName, finalScore)
        }

        // Play again
        playAgainButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Return to main menu
        mainMenuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
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

    private fun submitScoreToFirebase(playerName: String, score: Int) {
        try {
            val database = FirebaseDatabase.getInstance()
            val scoresRef = database.getReference("leaderboard")

            val scoreEntry = mapOf(
                "name" to playerName,
                "score" to score,
                "timestamp" to System.currentTimeMillis()
            )

            scoresRef.push().setValue(scoreEntry)
                .addOnSuccessListener {
                    Toast.makeText(this, "Score submitted!", Toast.LENGTH_SHORT).show()
                    findViewById<Button>(R.id.submitScoreButton).isEnabled = false
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to submit score: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Firebase not configured: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

