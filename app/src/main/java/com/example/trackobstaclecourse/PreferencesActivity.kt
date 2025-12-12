package com.example.trackobstaclecourse

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.log

class PreferencesActivity () : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MainActivity.currentTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.preferencesContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("TrackRunnerPrefs", MODE_PRIVATE)

        // Initialize views
        val themeSpinner = findViewById<Spinner>(R.id.themeSpinner)
        val livesSeekBar = findViewById<SeekBar>(R.id.livesSeekBar)
        val livesValueText = findViewById<TextView>(R.id.livesValueText)
        val difficultySpinner = findViewById<Spinner>(R.id.difficultySpinner)
        val vibrationSwitch = findViewById<Switch>(R.id.vibrationSwitch)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val backButton = findViewById<Button>(R.id.backButton)

        // Set up theme spinner
        val themes = arrayOf("Neon", "Retro", "Minimal")
        val themeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, themes)
        themeSpinner.adapter = themeAdapter
        when (MainActivity.currentTheme) {
            R.style.Base_Theme_TrackObstacleCourse -> themeSpinner.setSelection(0)
            R.style.Retro_Theme_TrackObstacleCourse -> themeSpinner.setSelection(1)
            R.style.Minimal_Theme_TrackObstacleCourse -> themeSpinner.setSelection(2)
            else -> themeSpinner.setSelection(0)
        }

        // Set up difficulty spinner
        val difficulties = arrayOf("Easy", "Normal", "Hard")
        val difficultyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, difficulties)
        difficultySpinner.adapter = difficultyAdapter

        // Load saved preferences
        val savedLives = sharedPreferences.getInt("starting_lives", 3)
        val savedDifficulty = sharedPreferences.getInt("difficulty_index", 1)
        val savedVibration = sharedPreferences.getBoolean("vibration_enabled", true)

        livesSeekBar.progress = savedLives - 1 // SeekBar is 0-indexed, lives are 1-5
        livesValueText.text = savedLives.toString()
        difficultySpinner.setSelection(savedDifficulty)
        vibrationSwitch.isChecked = savedVibration

        // SeekBar listener (New GUI Component with listener)
        livesSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val lives = progress + 1 // Convert 0-4 to 1-5
                livesValueText.text = lives.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Save button
        saveButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            when (themeSpinner.selectedItemPosition) {
                0 -> MainActivity.currentTheme = R.style.Base_Theme_TrackObstacleCourse
                1 -> MainActivity.currentTheme = R.style.Retro_Theme_TrackObstacleCourse
                2 -> MainActivity.currentTheme = R.style.Minimal_Theme_TrackObstacleCourse
            }
            Log.w("PreferencesActivity", MainActivity.currentTheme.toString())
            setTheme(MainActivity.currentTheme)
//            editor.putInt("theme_index", themeSpinner.selectedItemPosition)
            editor.putInt("starting_lives", livesSeekBar.progress + 1)
            editor.putInt("difficulty_index", difficultySpinner.selectedItemPosition)
            editor.putBoolean("vibration_enabled", vibrationSwitch.isChecked)

            recreate()
            editor.apply()

            when (MainActivity.currentTheme) {
                R.style.Base_Theme_TrackObstacleCourse -> themeSpinner.setSelection(0)
                R.style.Retro_Theme_TrackObstacleCourse -> themeSpinner.setSelection(1)
                R.style.Minimal_Theme_TrackObstacleCourse -> themeSpinner.setSelection(2)
                else -> themeSpinner.setSelection(0)
            }

            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Back button
        backButton.setOnClickListener {
            finish()
        }
    }
}
