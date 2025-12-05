package com.example.trackobstaclecourse

import android.content.res.Resources

/**
 * Model class for managing game obstacles and player state.
 * Part of the MVC architecture.
 */
class Obstacles {

    val width = Resources.getSystem().displayMetrics.widthPixels
    val height = Resources.getSystem().displayMetrics.heightPixels
    var running = false

    var obstacles: ArrayList<Obstacle> = ArrayList()

    /**
     * Get the X coordinate for a given track.
     * @param track Track number (0, 1, or 2)
     * @return X coordinate for the track center
     */
    fun getX(track: Int): Int {
        return when (track) {
            0 -> (width / 3) + 15
            1 -> (width / 2)
            2 -> (2 * (width / 3)) - 15
            else -> (width / 2)
        }
    }

    /**
     * Inner class representing a single obstacle on a track.
     */
    inner class Obstacle(trackIn: Int, yIn: Int) {
        private var track: Int = trackIn
        private var yPos: Int = yIn

        fun getTrack(): Int = track
        fun getY(): Int = yPos
        fun setY(newY: Int) { yPos = newY }
    }

    /**
     * Companion object for player state.
     * Acts as a singleton for player data.
     */
    companion object Player {
        var track: Int = 1  // Start in middle track
        var yPos: Int = ((Resources.getSystem().displayMetrics.heightPixels / 8) * 7)
        var avatar: String = ""
        var lives: Int = 3
        var score: Int = 0

        fun reset(startingLives: Int) {
            track = 1
            lives = startingLives
            score = 0
        }

        fun moveLeft() {
            if (track > 0) track--
        }

        fun moveRight() {
            if (track < 2) track++
        }

        fun hit(): Boolean {
            lives--
            return lives <= 0
        }
    }

    fun onCreate(avatar: String, lives: Int) {
        running = true
        Player.lives = lives
        Player.avatar = avatar
        obstacles.clear()
    }

    fun destroyObstacle(itemToRM: Obstacle) {
        obstacles.remove(itemToRM)
    }

    fun createObstacle() {
        obstacles.add(Obstacle((0..2).random(), (height / 8)))
    }

    fun updateObstacles(speed: Int): List<Obstacle> {
        val toRemove = mutableListOf<Obstacle>()
        for (obstacle in obstacles) {
            obstacle.setY(obstacle.getY() + speed)
            if (obstacle.getY() > height) {
                toRemove.add(obstacle)
            }
        }
        return toRemove
    }

    fun checkCollision(): Boolean {
        for (obstacle in obstacles) {
            if (obstacle.getTrack() == Player.track) {
                val playerTop = Player.yPos - 40
                val playerBottom = Player.yPos + 40
                val obstacleTop = obstacle.getY() - 35
                val obstacleBottom = obstacle.getY() + 35

                if (obstacleBottom >= playerTop && obstacleTop <= playerBottom) {
                    obstacles.remove(obstacle)
                    return true
                }
            }
        }
        return false
    }
}

