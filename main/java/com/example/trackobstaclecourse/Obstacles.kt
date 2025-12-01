package com.example.trackobstaclecourse

import android.content.res.Resources

class Obstacles {

    val width = Resources.getSystem().displayMetrics.widthPixels
    val height = Resources.getSystem().displayMetrics.heightPixels
    var running = false

    var obstacles : ArrayList<Obstacle> = ArrayList<Obstacle>()

    fun getX (row : Int) : Int {
        when (row) {
            0 -> return (width / 3) + 15
            1 -> return (width / 2)
            2 -> return (2 * (width / 3)) - 15
        }
        return (width / 2)
    }

    // Potental thing of note, if we have a value for what track the obstacle
    // is, do we need an x value?
    inner class Obstacle (trackIn : Int, yIn: Int) {

        private var track : Int = trackIn // determines which track the obstacle will be on
        private var yPos : Int = yIn // y position

        fun returnTrack () : Int {return track}
        fun returnY () : Int {return yPos}
    }

    // Potental thing of note, if we have a value for what track the obstacle
    // is, do we need an x value? We could just store the track value which
    // would then just tell us where the player is. We could have a switch
    // function for x and just call it whenever we want the x value
    companion object player {
        var track : Int = 1
        var yPos : Int = ((Resources.getSystem().displayMetrics.heightPixels / 8) * 7)
        var avatar : String = ""
        var lives : Int = 1
    }

    fun onCreate (avatar : String, lives : Int) {
        running = true
        player.lives = lives
    }

    fun destroyObsticle (itemToRM : Obstacle) {
        obstacles.remove(itemToRM)
    }

    fun createObsticle () {
        obstacles.add(Obstacle((0..2).random(), (height / 8)))
    }

    

}
