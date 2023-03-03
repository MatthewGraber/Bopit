package com.example.project1

//
//References:
//https://developer.android.com/guide/topics/sensors/sensors_motion
//https://medium.com/@olajhidey/working-with-countdown-timer-in-android-studio-using-kotlin-39fd7826e205
//

//LOG:
// 02122023 - This has the initial config for working with accel to determine if shaking.
// 02132023 - Added in switching between shaking and bopping.
// 02222023 - Twisting and shaking separated and bopping updated with multicolors

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.math.sqrt
import kotlin.system.exitProcess


class Game : AppCompatActivity(), SensorEventListener, View.OnClickListener
{
    //variable for sensor
    private lateinit var sensorManager: SensorManager
    //variables for shake it
    var accel = 0f
    var curraccel = 0f
    var prevaccel = 0f
    //variables for twist it
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    var preOr = 0f
    var CurrOr = 0f
    //variables for textbox
    private lateinit var comm: TextView
    //variable for scoring
    var score = 0;
    //variables for timer
    lateinit var gametimer: CountDownTimer
    var START_MILLI_SECONDS = 10000L
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L

    // Sound players
    var mMediaPlayer: MediaPlayer? = null
    var backgroundPlayer: MediaPlayer? = null;

    // Sound resources


//    val twistSound = resources.getIdentifier(
//        R.raw.twistit.toString(),
//        "raw", packageName)
//    val failureSound = resources.getIdentifier(
//        R.raw.failure.toString(),
//        "raw", packageName)
//    val backgroundMusic = resources.getIdentifier(
//        R.raw.fanfare.toString(),
//        "raw", packageName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        comm = findViewById < TextView >(R.id.textView)

        val rbutton = findViewById<Button>(R.id.redbutton)
        rbutton.setOnClickListener(this)
        val ybutton = findViewById<Button>(R.id.yellowbutton)
        ybutton.setOnClickListener(this)
        val gbutton = findViewById<Button>(R.id.greenbutton)
        gbutton.setOnClickListener(this)
        val bbutton = findViewById<Button>(R.id.bluebutton)
        bbutton.setOnClickListener(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)}
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_FASTEST) }

        // Begin the background music
//        backgroundPlayer = MediaPlayer.create(this, backgroundMusic)
//        backgroundPlayer!!.isLooping = true;
//        backgroundPlayer!!.start();

        playGame(START_MILLI_SECONDS)
    }

    private fun randomize() {
        var rand = (0..1).random()
        if(rand == 0){comm.text = "Twist It"}
        else if(rand==1){BopIt()}
        else {
            comm.text = "Shake It";
            ShakeIt()
            val shakeSound = resources.getIdentifier(
                R.raw.shakeit.toString(),
                "raw", packageName)
            mMediaPlayer = MediaPlayer.create(this, shakeSound);
            mMediaPlayer!!.start();
        }
//        comm.text = "Shake It"
//        ShakeIt()
    }

    private lateinit var color: String
    private fun BopIt() {
        color = arrayOf("red", "blue", "green", "yellow").random()

        // Color sounds
        val blueSound = resources.getIdentifier(
            R.raw.blue.toString(),
            "raw", packageName)
        val greenSound = resources.getIdentifier(
            R.raw.green.toString(),
            "raw", packageName)
        val redSound = resources.getIdentifier(
            R.raw.red.toString(),
            "raw", packageName)
        val yellowSound = resources.getIdentifier(
            R.raw.yellow.toString(),
            "raw", packageName)
        val fanfare = resources.getIdentifier(
            R.raw.fanfare.toString(),
            "raw", packageName)
        val soundMap = mapOf("red" to redSound, "blue" to blueSound, "green" to greenSound, "yellow" to yellowSound)
        mMediaPlayer = soundMap[color]?.let { MediaPlayer.create(this, it) }
        mMediaPlayer!!.start();

        var task = "Bop It: $color"
        comm.text = task
    }
    override fun onClick(v:View) {
        if(v.id==R.id.bluebutton && color == "blue"){
            gametimer.cancel()
            isRunning = false
            score++
            playGame(START_MILLI_SECONDS)
        }
        else if(v.id==R.id.bluebutton && color != "blue"){
            gametimer.cancel()
            isRunning = false
            endGame()
        }
        else if(v.id==R.id.redbutton && color == "red"){
            gametimer.cancel()
            isRunning = false
            score++
            playGame(START_MILLI_SECONDS)
        }
        else if(v.id==R.id.redbutton && color != "red"){
            gametimer.cancel()
            isRunning = false
            endGame()
        }
        else if(v.id==R.id.yellowbutton && color == "yellow"){
            gametimer.cancel()
            isRunning = false
            score++
            playGame(START_MILLI_SECONDS)
        }
        else if(v.id==R.id.yellowbutton && color != "yellow"){
            gametimer.cancel()
            isRunning = false
            endGame()
        }
        else if(v.id==R.id.greenbutton && color == "green"){
            gametimer.cancel()
            isRunning = false
            score++
            playGame(START_MILLI_SECONDS)
        }
        else if(v.id==R.id.greenbutton && color != "green"){
            gametimer.cancel()
            isRunning = false
            endGame()
        }
    }

    private fun ShakeIt() {
        accel = 10f
        curraccel = SensorManager.GRAVITY_EARTH
        prevaccel = SensorManager.GRAVITY_EARTH
    }
    override fun onSensorChanged(event:SensorEvent) {
        var rotdeg: Double = 0.0

        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            //code for twist it
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)

            //code for shake it
            prevaccel = curraccel
            val x = event.values[0] //Accel F along x axis (including gravity)
            val y = event.values[1] //Accel F along y axis (including gravity)
            val z = event.values[2] //Accel F along z axis (including gravity)
            curraccel = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            accel = accel * 0.9f + (curraccel - prevaccel)
        }
        else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        //code for twist it
        preOr = CurrOr
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        CurrOr = orientationAngles[0]*orientationAngles[0]+
                orientationAngles[1]* orientationAngles[1]+
                orientationAngles[2]*orientationAngles[2]
        rotdeg = Math.toDegrees((CurrOr-preOr).toDouble())

        if(comm.text == "Shake It" && accel > 10 && rotdeg < 45){
            gametimer.cancel()
            isRunning = false
            score++
            playGame(START_MILLI_SECONDS)
        }
        else if(comm.text == "Shake It" && rotdeg > 45){
            gametimer.cancel()
            isRunning = false
            endGame()
        }
        else if(comm.text == "Twist It" && accel < 10 && rotdeg > 45){
            gametimer.cancel()
            isRunning = false
            score++
            playGame(START_MILLI_SECONDS)
        }
        else if(comm.text == "Twist It" && accel > 10){
            gametimer.cancel()
            isRunning = false
            endGame()
        }
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    fun playGame(time_in_seconds: Long) {
        randomize()
        // TODO: Play the correct sound
        gametimer = object : CountDownTimer(time_in_seconds, 100) {
            override fun onTick(millisUntilFinished: Long) {
                time_in_milli_seconds = millisUntilFinished
            }
            override fun onFinish() {
                endGame()
            }
        }
        gametimer.start()
        isRunning = true
    }
    fun endGame() {
        val intent = Intent(this, EndActivity::class.java)
        intent.putExtra("Score", score.toString());
        startActivity(intent)
        this@Game.finish()
        exitProcess(0)
    }
}