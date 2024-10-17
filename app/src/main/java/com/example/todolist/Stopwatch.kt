package com.example.todolist


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityStopwatchBinding
import kotlin.math.roundToInt

class Stopwatch : AppCompatActivity() {

    private lateinit var binding: ActivityStopwatchBinding
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the stopwatch layout instead of main layout
        binding = ActivityStopwatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up button click listeners
        binding.startStopButton.setOnClickListener { startStopTimer() }
        binding.resetButton.setOnClickListener { resetTimer() }

        // Prepare the service intent to start the TimerService
        serviceIntent = Intent(applicationContext, TimerService::class.java)

        // Register the receiver to listen for time updates from the TimerService
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.timeTV.text = getTimeStringFromDouble(time) // Update the displayed time
    }

    private fun startStopTimer() {
        if (timerStarted) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent) // Start the background TimerService

        // Update button text and icon when timer starts
        binding.startStopButton.text = "Stop"
        binding.startStopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_pause_24, 0, 0, 0)
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(serviceIntent) // Stop the background TimerService

        // Update button text and icon when timer stops
        binding.startStopButton.text = "Start"
        binding.startStopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_play_arrow_24, 0, 0, 0)
        timerStarted = false
    }

    // BroadcastReceiver to update the time on the screen
    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.timeTV.text = getTimeStringFromDouble(time) // Update the displayed time
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String =
        String.format("%02d:%02d:%02d", hour, min, sec)

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(updateTime) // Unregister the receiver to prevent memory leaks
    }
}
