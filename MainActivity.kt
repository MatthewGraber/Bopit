package com.example.bopit
import android.content.Intent
import android.media.MediaPlayer;
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button

class MainActivity : AppCompatActivity() {
    var mMediaPlayer: MediaPlayer? = null
    var backgroundPlayer: MediaPlayer? = null;

    var score = 0;

    enum class STATE {
        Waiting,
        Success,
        Failure
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playbt = this.findViewById<Button>(R.id.sound_button);
        val resId = resources.getIdentifier(R.raw.fanfare.toString(),
            "raw", packageName)

        // Sound resources
        val bopSound = resources.getIdentifier(R.raw.fanfare.toString(),
            "raw", packageName)
        val twistSound = resources.getIdentifier(R.raw.fanfare.toString(),
            "raw", packageName)
        val pullSound = resources.getIdentifier(R.raw.fanfare.toString(),
            "raw", packageName)
        val screamSound = resources.getIdentifier(R.raw.fanfare.toString(),
            "raw", packageName)
        val backgroundMusic = resources.getIdentifier(R.raw.fanfare.toString(),
            "raw", packageName)

        backgroundPlayer = MediaPlayer.create(this, backgroundMusic)
        backgroundPlayer!!.isLooping = true;
        backgroundPlayer!!.start();


        playbt.setOnClickListener {
            playSound(resId)
        }
    }


    fun playGame() {

        // TODO: Select a type of input to be used
        // TODO: Play the correct sound
        object : CountDownTimer(30000, 100) {

            override fun onTick(millisUntilFinished: Long) {
                // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000)
                if (true /*TODO: Good sensor input*/) {
                    state = STATE.Success;
                    score++;
                    playGame();
                }
                else if (false /*TODO: Bad sensor input*/) {
                    endGame();
                }
            }

            override fun onFinish() {
                endGame();
            }
        }.start();
    }

    fun endGame() {
        val intent = Intent(this, EndActivity::class.java)
        intent.putExtra("Score", score);
        startActivity(intent)
    }

    fun playSound(sound: Int) {
        mMediaPlayer = MediaPlayer.create(this, sound)
        //mMediaPlayer!!.isLooping = true
        mMediaPlayer!!.start()
    }

    // 2. Pause playback
    fun pauseSound(media: MediaPlayer) {
        if (media?.isPlaying == true) media?.pause()
    }

    // 3. Stops playback
    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    // 4. Destroys the MediaPlayer instance when the app is closed
    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        if (backgroundPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }
}