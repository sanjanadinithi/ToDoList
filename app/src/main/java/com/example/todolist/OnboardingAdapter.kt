package com.example.todolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.OnboardingPageBinding

class OnboardingAdapter(private val context: OnboardingActivity) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    // List of onboarding screens
    private val onboardingScreens = listOf(
        OnboardingScreen(R.drawable.image1, "Welcome", "Welcome to the Todo app!"),
        OnboardingScreen(R.drawable.image2, "Organize", "Organize your tasks efficiently."),
        OnboardingScreen(R.drawable.image3, "Stay Productive", "Stay productive and on track!")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = OnboardingPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingScreens[position])
    }

    override fun getItemCount(): Int = onboardingScreens.size

    inner class OnboardingViewHolder(private val binding: OnboardingPageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(screen: OnboardingScreen) {
            binding.imageView.setImageResource(screen.imageRes)
            binding.titleTextView.text = screen.title
            binding.descriptionTextView.text = screen.description
        }
    }
}
