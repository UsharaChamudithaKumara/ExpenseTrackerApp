package com.example.financetracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.financetracker.databinding.ActivityBudgetBinding
import com.google.android.material.navigation.NavigationBarView

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
        val savedBudget = prefs.getFloat("monthly_budget", 0f)

        if (savedBudget > 0) {
            binding.etBudget.setText(savedBudget.toString())
            binding.tvStatus.text = "Current Budget: LKR %.2f".format(savedBudget)
        }

        binding.btnSaveBudget.setOnClickListener {
            val budget = binding.etBudget.text.toString().toFloatOrNull()
            if (budget != null && budget > 0) {
                prefs.edit().putFloat("monthly_budget", budget).apply()
                binding.tvStatus.text = "Budget saved: LKR %.2f".format(budget)
                Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show()

                // 🔥 Show Notification after saving
                NotificationHelper.showBudgetSavedNotification(this)
            } else {
                binding.tvStatus.text = "Enter a valid budget to save"
                Toast.makeText(this, "Invalid budget entered", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdateBudget.setOnClickListener {
            val budget = binding.etBudget.text.toString().toFloatOrNull()
            if (budget != null && budget > 0) {
                prefs.edit().putFloat("monthly_budget", budget).apply()
                binding.tvStatus.text = "Budget updated: LKR %.2f".format(budget)
                Toast.makeText(this, "Budget updated successfully!", Toast.LENGTH_SHORT).show()

                // 🔥 Show Notification after updating
                NotificationHelper.showBudgetSavedNotification(this)
            } else {
                binding.tvStatus.text = "Enter a valid budget to update"
                Toast.makeText(this, "Invalid budget entered", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDeleteBudget.setOnClickListener {
            prefs.edit().remove("monthly_budget").apply()
            binding.etBudget.setText("")
            binding.tvStatus.text = "Budget deleted"
            Toast.makeText(this, "Budget deleted successfully!", Toast.LENGTH_SHORT).show()
        }

        // Bottom Navigation setup
        binding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, Main2Activity::class.java))
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_budget -> {
                    // Already on this page
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        })

        // Highlight the current page
        binding.bottomNavigation.selectedItemId = R.id.nav_budget
    }
}
