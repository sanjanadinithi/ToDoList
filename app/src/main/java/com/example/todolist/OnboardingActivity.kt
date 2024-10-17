package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.todolist.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the ViewPager with the adapter
        val adapter = OnboardingAdapter(this)
        binding.viewPager.adapter = adapter

        // Show the finish button only on the last onboarding screen
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Show the button only on the last screen
                binding.finishButton.visibility = if (position == adapter.itemCount - 1) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })

        // Hide the button initially
        binding.finishButton.visibility = View.GONE

        // Set up the button click listener to launch MainActivity
        binding.finishButton.setOnClickListener {
            // Save onboarding completion status in SharedPreferences
            val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("onboarding_complete", true)
                apply()
            }

            // Launch the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Close the OnboardingActivity
            finish()
        }
    }
}
