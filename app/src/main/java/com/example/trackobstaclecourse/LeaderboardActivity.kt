package com.example.trackobstaclecourse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var emptyText: TextView
    private val scoresList = mutableListOf<ScoreEntry>()

    data class ScoreEntry(
        val name: String = "",
        val score: Int = 0,
        val timestamp: Long = 0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MainActivity.currentTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.leaderboardContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        recyclerView = findViewById(R.id.leaderboardRecyclerView)
        loadingProgress = findViewById(R.id.loadingProgress)
        emptyText = findViewById(R.id.emptyText)
        val backButton = findViewById<Button>(R.id.backButton)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LeaderboardAdapter(scoresList)

        // Load leaderboard data
        loadLeaderboard()

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadLeaderboard() {
        loadingProgress.visibility = View.VISIBLE
        emptyText.visibility = View.GONE
        recyclerView.visibility = View.GONE

        try {
            val database = FirebaseDatabase.getInstance()
            val scoresRef = database.getReference("leaderboard")

            scoresRef.orderByChild("score").limitToLast(50)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        scoresList.clear()

                        for (scoreSnapshot in snapshot.children) {
                            val name = scoreSnapshot.child("name").getValue(String::class.java) ?: ""
                            val score = scoreSnapshot.child("score").getValue(Int::class.java) ?: 0
                            val timestamp = scoreSnapshot.child("timestamp").getValue(Long::class.java) ?: 0

                            scoresList.add(ScoreEntry(name, score, timestamp))
                        }

                        // Sort by score descending
                        scoresList.sortByDescending { it.score }

                        loadingProgress.visibility = View.GONE

                        if (scoresList.isEmpty()) {
                            emptyText.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            emptyText.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        loadingProgress.visibility = View.GONE
                        emptyText.visibility = View.VISIBLE
                        emptyText.text = "Failed to load: ${error.message}"
                    }
                })
        } catch (e: Exception) {
            loadingProgress.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
            emptyText.text = "Firebase not configured"
        }
    }

    inner class LeaderboardAdapter(private val scores: List<ScoreEntry>) :
        RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val rankText: TextView = view.findViewById(R.id.rankText)
            val nameText: TextView = view.findViewById(R.id.nameText)
            val scoreText: TextView = view.findViewById(R.id.scoreText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_leaderboard, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val entry = scores[position]
            holder.rankText.text = "#${position + 1}"
            holder.nameText.text = entry.name
            holder.scoreText.text = "${entry.score} pts"

            // Highlight top 3
            when (position) {
                0 -> {
                    holder.rankText.setTextColor(getColor(R.color.neon_yellow))
                    holder.nameText.setTextColor(getColor(R.color.neon_yellow))
                }
                1 -> {
                    holder.rankText.setTextColor(getColor(R.color.text_secondary))
                    holder.nameText.setTextColor(getColor(R.color.text_secondary))
                }
                2 -> {
                    holder.rankText.setTextColor(getColor(R.color.neon_orange))
                    holder.nameText.setTextColor(getColor(R.color.neon_orange))
                }
                else -> {
                    holder.rankText.setTextColor(getColor(R.color.text_primary))
                    holder.nameText.setTextColor(getColor(R.color.text_primary))
                }
            }
        }

        override fun getItemCount() = scores.size
    }
}

