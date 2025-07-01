package com.example.financetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.financetracker.databinding.ActivityOnboardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnBoardingAdapter(this)
        binding.viewPager.adapter = adapter

        // Next Button Click
        binding.btnNext.setOnClickListener {
            val nextPage = binding.viewPager.currentItem + 1
            if (nextPage < 3) { // only 3 pages (0,1,2)
                binding.viewPager.currentItem = nextPage
            }
        }

        // Get Started Button Click
        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // ViewPager page change listener
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnGetStarted.visibility = View.VISIBLE
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnGetStarted.visibility = View.GONE
                }
            }
        })
    }
}
