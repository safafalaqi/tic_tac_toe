package com.example.tictactoe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import java.util.*
import kotlin.collections.ArrayList

class GameActivity : AppCompatActivity() {

    var is2players=false
    var isStart=false
    var isMute=false
    var playerOne=false
    var playerTwo=false
    var p1WinCount=0
    var p2WinCount=0

    //for setting scores
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var bt11: ImageButton
    private lateinit var bt12: ImageButton
    private lateinit var bt13: ImageButton
    private lateinit var bt21: ImageButton
    private lateinit var bt22: ImageButton
    private lateinit var bt23: ImageButton
    private lateinit var bt31: ImageButton
    private lateinit var bt32: ImageButton
    private lateinit var bt33: ImageButton

    private lateinit var tvWho: TextView

    private lateinit var btHome: ImageButton
    private lateinit var btBack: ImageButton
    private lateinit var btMusic: ImageButton

    private lateinit  var buttonArray:ArrayList<ImageButton>

    private  var arrayState= arrayListOf(
        arrayListOf('a','b','c'),
        arrayListOf('d','e','f'),
        arrayListOf('j','h','i')
    ) //z for computer x for player 1 o for player 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //hide action bar
        supportActionBar?.hide()

        //to set score for first player
        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        //get if 2 player or single player
        is2players = intent.getBooleanExtra("type", false)

        bt11 = findViewById(R.id.bt1)
        bt12 = findViewById(R.id.bt2)
        bt13 = findViewById(R.id.bt3)
        bt21 = findViewById(R.id.bt4)
        bt22 = findViewById(R.id.bt5)
        bt23 = findViewById(R.id.bt6)
        bt31 = findViewById(R.id.bt7)
        bt32 = findViewById(R.id.bt8)
        bt33 = findViewById(R.id.bt9)

        buttonArray= arrayListOf( bt11, bt12 , bt13 , bt21, bt22, bt23, bt31 , bt32, bt33 )

        for(button in buttonArray){

            setOnClickListener(button)
        }


        tvWho =findViewById<TextView>(R.id.tvWhoPlay)
        tvWho.text="Player 1"

        btHome = findViewById(R.id.btHome)
        btBack = findViewById(R.id.btBack)
        btMusic = findViewById(R.id.btSoundG)

        //home button
        btHome.setOnClickListener {
            val intent = Intent(this@GameActivity, MainActivity::class.java)
            startActivity(intent)
        }
        //back button
        btBack.setOnClickListener {
            val intent = Intent(this@GameActivity, MainActivity::class.java)
            startActivity(intent)
        }
        //for mute button
        btMusic.setOnClickListener {
            if (!isMute) {
                btMusic.setBackgroundResource(R.drawable.mute)
                MusicManager.pauseAudio()
                isMute=true
            } else {
                btMusic.setBackgroundResource(R.drawable.music)
                MusicManager.playAudio(this, R.raw.background)
                isMute=false
            }
        }
    }
   //this to handle the button when it is being clicked and decide who wins
    private fun setOnClickListener(button: ImageButton){
        button.setOnClickListener{
            clickSound()
            if(!isStart){
                isStart=true
                button.setBackgroundResource(R.drawable.xbutton)
                button.isEnabled = false
                buttonClicked(button,'x')
                tvWho.text="Player 2"

                if(is2players)
                playerTwo=true
                else{
                    tvWho.text="Bot"
                    botClicked()
                    playerOne=true
                }

            }else if(playerOne)
            {
                button.setBackgroundResource(R.drawable.xbutton)
                button.isEnabled = false
                buttonClicked(button,'x')
                tvWho.text="Player 2"

                if(check())
                {
                  whoWin('x')
                }else if(is2players) {
                    playerTwo = true
                    playerOne=false
                    isEnd()
                }else
                {

                    tvWho.text="Bot"
                    botClicked()
                    playerOne=true
                    isEnd()
                }

            }else if(playerTwo){
                button.setBackgroundResource(R.drawable.obutton)
                button.isEnabled = false
                buttonClicked(button,'o')
                tvWho.text="Player 1"
                if(check())
                {
                    whoWin('o')
                }
                playerTwo=false
                playerOne=true

                isEnd()

            }

        }
    }

    //to show who wins and ask the user to choose either to play again or stop
    private fun whoWin(c: Char) {
        var again= findViewById<ImageButton>(R.id.btAgainWin)
        var close= findViewById<ImageButton>(R.id.btClose)

        if(c=='x'){
            winSound()
            findViewById<RelativeLayout>(R.id.llwinlose).isVisible = true
            findViewById<LinearLayout>(R.id.llBoard).isVisible = false
            findViewById<TextView>(R.id.tvWhoWin).setText("Player 1 Win!")
            p1WinCount+=1
            setScore("score",p1WinCount)
            findViewById<TextView>(R.id.p1Score).setText("player2:$p1WinCount")
            again.setOnClickListener{
                resetGame()
                findViewById<RelativeLayout>(R.id.llwinlose).isVisible = false
                findViewById<LinearLayout>(R.id.llBoard).isVisible = true
            }

            close.setOnClickListener{
                findViewById<RelativeLayout>(R.id.llwinlose).isVisible = false
                findViewById<LinearLayout>(R.id.llBoard).isVisible = true
            }

        }
        else{
            winSound()
            findViewById<RelativeLayout>(R.id.llwinlose).isVisible = true
            findViewById<LinearLayout>(R.id.llBoard).isVisible = false
            findViewById<TextView>(R.id.tvWhoWin).setText("Player 2 Win!")
            p2WinCount+=1
            findViewById<TextView>(R.id.p2Score).setText("player2:$p2WinCount")
            again.setOnClickListener{
                resetGame()
                findViewById<RelativeLayout>(R.id.llwinlose).isVisible = false
                findViewById<LinearLayout>(R.id.llBoard).isVisible = true
            }

            close.setOnClickListener{
                findViewById<RelativeLayout>(R.id.llwinlose).isVisible = false
                findViewById<LinearLayout>(R.id.llBoard).isVisible = true
            }
        }
    }

    //it is going to check if all the buttons are checked and no one wins
    fun isEnd(){
        var count=0
     for(i in  buttonArray)
     {
         if(i.isEnabled==false)
             count++
     }
     if(count==9){
         noOneWin()
     }
 }

    //this to show the layout for if no one wins
    fun noOneWin()  {
        var again= findViewById<ImageButton>(R.id.btAgainNo)
        var close= findViewById<ImageButton>(R.id.btCloseNo)
        findViewById<RelativeLayout>(R.id.llNoOneWin).isVisible=true
        findViewById<LinearLayout>(R.id.llBoard).isVisible = false

        again.setOnClickListener{
            resetGame()
            findViewById<RelativeLayout>(R.id.llNoOneWin).isVisible = false
            findViewById<LinearLayout>(R.id.llBoard).isVisible = true
        }

        close.setOnClickListener{
            findViewById<RelativeLayout>(R.id.llNoOneWin).isVisible = false
            findViewById<LinearLayout>(R.id.llBoard).isVisible = true
        }

    }

    //when computer click just choose random button
    fun botClicked() {
        var found = false
        var bt: ImageButton? = null
        for (i in buttonArray){
            if (i.isEnabled) {
                bt=i
                found=true
            }
        }
        if(found){
            bt!!.setBackgroundResource(R.drawable.obutton)
            bt.isEnabled=false
            buttonClicked(bt,'o')
            tvWho.text="Player 1"
            if(check())
            {
                whoWin('o')
            }
            playerOne=true
        }
    }

    //this to add the character to the arrayStatus
    private fun buttonClicked(button: ImageButton,char:Char) {
        when(button){
            bt11 -> arrayState[0][0]=char
            bt12 -> arrayState[0][1]=char
            bt13 -> arrayState[0][2]=char
            bt21 -> arrayState[1][0]=char
            bt22 -> arrayState[1][1]=char
            bt23 -> arrayState[1][2]=char
            bt31 -> arrayState[2][0]=char
            bt32 -> arrayState[2][1]=char
            bt33 -> arrayState[2][2]=char
        }
    }

    //this function for checking the wining positions
    private fun check(): Boolean{
        if(arrayState[0][0]==arrayState[0][1] && arrayState[0][1]==arrayState[0][2]){return true}
        if(arrayState[1][0]==arrayState[1][1] && arrayState[1][1]==arrayState[1][2]){return true}
        if(arrayState[2][0]==arrayState[2][1] && arrayState[2][1]==arrayState[2][2]){return true}
        if(arrayState[0][0]==arrayState[1][0] && arrayState[1][0]==arrayState[2][0]){return true}
        if(arrayState[0][1]==arrayState[1][1] && arrayState[1][1]==arrayState[2][1]){return true}
        if(arrayState[0][2]==arrayState[1][2] && arrayState[1][2]==arrayState[2][2]){return true}
        if(arrayState[0][0]==arrayState[1][1] && arrayState[1][1]==arrayState[2][2]){return true}
        if(arrayState[0][2]==arrayState[1][1] && arrayState[1][1]==arrayState[2][0]){return true}
        return false
    }

   //this function is to reset the game
    fun resetGame(){
       for(button in buttonArray) {
           button.setBackgroundResource(R.drawable.buttonclear)
           button.isEnabled = true
       }
        arrayState= arrayListOf(
            arrayListOf('a','b','c'),
            arrayListOf('d','e','f'),
            arrayListOf('j','h','i')
        )
        is2players=false
        isStart=false
        isMute=false
        playerOne=false
        playerTwo=false

    }

    //for click sound
    private fun clickSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click)
        mediaPlayer.start()
    }

    //for win sound
    private fun winSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.win)
        mediaPlayer.start()
    }

    //for lost sound
    private fun lostSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.lose)
        mediaPlayer.start()
    }

    //for the music manager
    override fun onPause() {
        super.onPause()
        MusicManager.pauseAudio()
    }

    //for the music manager
    override fun onResume() {
        super.onResume()
        MusicManager.continuePlaying(this)
    }

    fun getScore(key: String): Int {
        val num = sharedPreferences.getInt(key, 0)
        return num
    }

    fun setScore(key: String,score:Int) {
        with(sharedPreferences.edit()) {
            putInt(key, score)
            apply()
        }
    }
}