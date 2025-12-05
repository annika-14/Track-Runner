package com.example.trackobstaclecourse

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.view.View
import kotlin.random.Random

class GameView(context: Context) : View(context) {

    interface GameListener {
        fun onScoreUpdate(newScore: Int)
        fun onLivesUpdate(lives: Int)
        fun onCollision()
        fun onGameOver(finalScore: Int)
    }

    var gameListener: GameListener? = null

    // Game state
    private var isRunning = false
    private var isPaused = false
    private var score = 0
    private var lives = 3
    private var gameTime = 0L // milliseconds elapsed
    private var obstacleSpawnInterval = 1500L // Start spawning every 1.5 seconds
    private var lastSpawnTime = 0L

    // Player state
    private var playerTrack = 1 // 0, 1, or 2 (middle track by default)
    private val playerSize = 80f
    private var playerY = 0f

    // Obstacles
    private val obstacles = mutableListOf<Obstacle>()
    private val obstacleSpeed = 12f // pixels per frame
    private val obstacleSize = 70f

    // Track positions (will be calculated based on view width)
    private val trackPositions = FloatArray(3)
    private val trackPadding = 60f

    // Paints
    private val trackPaint = Paint().apply {
        color = Color.parseColor("#00F5FF")
        alpha = 60
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val playerPaint = Paint().apply {
        color = Color.parseColor("#00F5FF")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val playerGlowPaint = Paint().apply {
        color = Color.parseColor("#00F5FF")
        alpha = 100
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val obstaclePaint = Paint().apply {
        color = Color.parseColor("#FF1493")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val obstacleGlowPaint = Paint().apply {
        color = Color.parseColor("#FF1493")
        alpha = 100
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Game loop handler
    private val handler = Handler(Looper.getMainLooper())
    private val gameLoopRunnable = object : Runnable {
        override fun run() {
            if (isRunning && !isPaused) {
                updateGame()
                invalidate()
                handler.postDelayed(this, 16) // ~60 FPS
            }
        }
    }

    data class Obstacle(
        var track: Int,
        var y: Float
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculate track positions
        val usableWidth = w - (trackPadding * 2)
        trackPositions[0] = trackPadding + (usableWidth / 6)
        trackPositions[1] = w / 2f
        trackPositions[2] = w - trackPadding - (usableWidth / 6)

        // Set player Y position (near bottom)
        playerY = h - 200f

        // Start the game
        startGame()
    }

    private fun startGame() {
        isRunning = true
        isPaused = false
        score = 0
        gameTime = 0
        lastSpawnTime = 0
        obstacleSpawnInterval = 1500L
        obstacles.clear()
        playerTrack = 1

        gameListener?.onScoreUpdate(score)
        gameListener?.onLivesUpdate(lives)

        handler.post(gameLoopRunnable)
    }

    private fun updateGame() {
        gameTime += 16 // Add frame time

        // Increase difficulty over time
        if (gameTime > 0 && gameTime % 10000 < 16) { // Every 10 seconds
            obstacleSpawnInterval = maxOf(400L, obstacleSpawnInterval - 100)
        }

        // Spawn new obstacles
        if (gameTime - lastSpawnTime >= obstacleSpawnInterval) {
            spawnObstacle()
            lastSpawnTime = gameTime
        }

        // Update obstacles
        val iterator = obstacles.iterator()
        while (iterator.hasNext()) {
            val obstacle = iterator.next()
            obstacle.y += obstacleSpeed

            // Check collision with player
            if (checkCollision(obstacle)) {
                iterator.remove()
                handleCollision()
                continue
            }

            // Remove if off screen
            if (obstacle.y > height + obstacleSize) {
                iterator.remove()
                score += 10
                gameListener?.onScoreUpdate(score)
            }
        }
    }

    private fun spawnObstacle() {
        val track = Random.nextInt(3)
        obstacles.add(Obstacle(track, -obstacleSize))
        
        // Occasionally spawn on multiple tracks for harder difficulty
        if (score > 200 && Random.nextFloat() < 0.3f) {
            val secondTrack = (track + 1 + Random.nextInt(2)) % 3
            obstacles.add(Obstacle(secondTrack, -obstacleSize))
        }
    }

    private fun checkCollision(obstacle: Obstacle): Boolean {
        if (obstacle.track != playerTrack) return false

        val playerTop = playerY - playerSize / 2
        val playerBottom = playerY + playerSize / 2
        val obstacleTop = obstacle.y - obstacleSize / 2
        val obstacleBottom = obstacle.y + obstacleSize / 2

        return obstacleBottom >= playerTop && obstacleTop <= playerBottom
    }

    private fun handleCollision() {
        lives--
        gameListener?.onCollision()
        gameListener?.onLivesUpdate(lives)

        if (lives <= 0) {
            endGame()
        }
    }

    private fun endGame() {
        isRunning = false
        handler.removeCallbacks(gameLoopRunnable)
        gameListener?.onGameOver(score)
    }

    fun movePlayerLeft() {
        if (playerTrack > 0) {
            playerTrack--
        }
    }

    fun movePlayerRight() {
        if (playerTrack < 2) {
            playerTrack++
        }
    }

    fun setLives(startingLives: Int) {
        lives = startingLives
    }

    fun pauseGame() {
        isPaused = true
    }

    fun resumeGame() {
        if (isPaused) {
            isPaused = false
            handler.post(gameLoopRunnable)
        }
    }

    fun stopGame() {
        isRunning = false
        handler.removeCallbacks(gameLoopRunnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background grid lines
        drawTracks(canvas)

        // Draw obstacles
        for (obstacle in obstacles) {
            drawObstacle(canvas, obstacle)
        }

        // Draw player
        drawPlayer(canvas)
    }

    private fun drawTracks(canvas: Canvas) {
        for (x in trackPositions) {
            canvas.drawLine(x, 0f, x, height.toFloat(), trackPaint)
        }

        // Draw horizontal grid lines
        val gridSpacing = height / 10f
        for (i in 0..10) {
            val y = i * gridSpacing
            canvas.drawLine(0f, y, width.toFloat(), y, trackPaint.apply { alpha = 30 })
        }
        trackPaint.alpha = 60 // Reset alpha
    }

    private fun drawPlayer(canvas: Canvas) {
        val playerX = trackPositions[playerTrack]

        // Draw glow effect
        canvas.drawCircle(playerX, playerY, playerSize / 2 + 15, playerGlowPaint)

        // Draw player as a diamond/triangle shape
        val halfSize = playerSize / 2
        canvas.drawCircle(playerX, playerY, halfSize, playerPaint)

        // Inner highlight
        val innerPaint = Paint(playerPaint).apply {
            color = Color.WHITE
            alpha = 150
        }
        canvas.drawCircle(playerX, playerY - halfSize / 4, halfSize / 3, innerPaint)
    }

    private fun drawObstacle(canvas: Canvas, obstacle: Obstacle) {
        val obstacleX = trackPositions[obstacle.track]

        // Draw glow
        canvas.drawCircle(obstacleX, obstacle.y, obstacleSize / 2 + 10, obstacleGlowPaint)

        // Draw obstacle as a square/diamond
        val halfSize = obstacleSize / 2
        val rect = RectF(
            obstacleX - halfSize,
            obstacle.y - halfSize,
            obstacleX + halfSize,
            obstacle.y + halfSize
        )
        canvas.drawRoundRect(rect, 10f, 10f, obstaclePaint)

        // Inner X pattern
        val linePaint = Paint().apply {
            color = Color.WHITE
            alpha = 200
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }
        val innerOffset = halfSize * 0.6f
        canvas.drawLine(
            obstacleX - innerOffset, obstacle.y - innerOffset,
            obstacleX + innerOffset, obstacle.y + innerOffset,
            linePaint
        )
        canvas.drawLine(
            obstacleX + innerOffset, obstacle.y - innerOffset,
            obstacleX - innerOffset, obstacle.y + innerOffset,
            linePaint
        )
    }
}

