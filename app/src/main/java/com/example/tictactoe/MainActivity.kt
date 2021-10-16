package com.example.tictactoe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    //for getting scores
    private lateinit var sharedPreferences: SharedPreferences
    var isMute=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //hide actionbar
        supportActionBar?.hide()
        //click sound
        val click = MediaPlayer.create(this, R.raw.click)

        val score= findViewById<TextView>(R.id.tvTotalScore)

        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        score.text="Scores:"+getScore("score")

        startMusic()
        val btSingle= findViewById<ImageButton>(R.id.btSingle)
        val btTwo= findViewById<ImageButton>(R.id.bt2player)
        val btMusic= findViewById<ImageButton>(R.id.btSound)

        btSingle.setOnClickListener{
            click.start()
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            intent.putExtra("type",false)
            startActivity(intent)
        }

        btTwo.setOnClickListener{
            click.start()
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            intent.putExtra("type",true)
            startActivity(intent)
        }

        btMusic.setOnClickListener{
            if(!isMute) {
                btMusic.setBackgroundResource(R.drawable.mute)
                MusicManager.pauseAudio()
                isMute=true
            }else
            {
                btMusic.setBackgroundResource(R.drawable.music)
                MusicManager.playAudio(this, R.raw.background)
                isMute=false
            }
        }
    }

    private fun startMusic() {
        MusicManager.playAudio(this, R.raw.background)
    }

    override fun onPause() {
        super.onPause()
        MusicManager.pauseAudio()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.continuePlaying(this)
    }

    fun getScore(key: String): Int {
        val num = sharedPreferences.getInt(key, 0)
        return num
    }

}