package com.example.trackobstaclecourse

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var highScoreText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TrackRunnerPrefs", MODE_PRIVATE)

        // Initialize views
        val titleText = findViewById<TextView>(R.id.titleText)
        val subtitleText = findViewById<TextView>(R.id.subtitleText)
        highScoreText = findViewById(R.id.highScoreText)
        val startButton = findViewById<Button>(R.id.startButton)
        val preferencesButton = findViewById<Button>(R.id.preferencesButton)
        val leaderboardButton = findViewById<Button>(R.id.leaderboardButton)

        // Load AdMob banner
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Load and display high score
        updateHighScoreDisplay()

        // Set up button click listeners
        startButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        preferencesButton.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }

        leaderboardButton.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        // Add pulse animation to start button
        startButton.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startButton.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
            } else {
                startButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Update high score when returning from game
        updateHighScoreDisplay()
    }

    private fun updateHighScoreDisplay() {
        val highScore = sharedPreferences.getInt("high_score", 0)
        highScoreText.text = getString(R.string.high_score_format, highScore)
    }
}

