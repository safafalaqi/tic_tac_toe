package com.example.tictactoe

import android.content.Context
import android.media.MediaPlayer

object MusicManager{

    var mediaPlayer: MediaPlayer? = null
    var lastResource: Int? = null

    fun playAudio(c: Context, id: Int, isLooping: Boolean = true) {
        createMediaPlayer(c, id)

        mediaPlayer?.let {
            it.isLooping = isLooping

            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    private fun createMediaPlayer(c: Context, id: Int) {
        //stop the current music
        mediaPlayer?.stop()

        mediaPlayer = MediaPlayer.create(c, id)
        lastResource = id
    }

    fun continuePlaying(c: Context, resource: Int? = null) {
        resource?.let {
            if (lastResource != resource) {
                createMediaPlayer(c, resource)
            }
        }

        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
    }
    fun stopAudio() {
        mediaPlayer?.stop()
    }

}