package com.example.financetracker

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.financetracker.databinding.ActivitySettingsBinding
import com.google.android.material.navigation.NavigationBarView

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCurrencySpinner()

        // ✅ Export button
        binding.btnExportData.setOnClickListener {
            exportData()
        }

        // ✅ Restore button
        binding.btnRestoreData.setOnClickListener {
            restoreData()
        }

        // ✅ Clear Data button
        binding.btnClearData.setOnClickListener {
            clearAllData()
        }

        // ✅ Logout button
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // ✅ Bottom Navigation
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
                    startActivity(Intent(this, BudgetActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    // Already here
                    true
                }
                else -> false
            }
        })

        binding.bottomNavigation.selectedItemId = R.id.nav_settings
    }

    private fun setupCurrencySpinner() {
        val currencies = arrayOf("LKR", "USD", "EUR", "GBP", "AUD")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = adapter

        val prefs = getSharedPreferences("fintrack_prefs", MODE_PRIVATE)
        val savedCurrency = prefs.getString("currency", "LKR")
        val selectedPosition = currencies.indexOf(savedCurrency)
        binding.spinnerCurrency.setSelection(selectedPosition)

        binding.spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedCurrency = currencies[position]
                prefs.edit().putString("currency", selectedCurrency).apply()
                Toast.makeText(this@SettingsActivity, "Currency set to $selectedCurrency", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun exportData() {
        try {
            val transactions = StorageHelper.getTransactions(this)
            if (transactions.isNotEmpty()) {
                StorageHelper.backupTransactions(this, transactions)
                Toast.makeText(this, "✅ Export successful! Transactions backed up.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "⚠️ No transactions to export.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreData() {
        try {
            val transactions = StorageHelper.loadBackupTransactions(this)
            if (transactions != null && transactions.isNotEmpty()) {
                StorageHelper.saveTransactions(this, transactions)
                Toast.makeText(this, "✅ Restore successful! Transactions restored.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "⚠️ No backup data found to restore.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearAllData() {
        try {
            StorageHelper.saveTransactions(this, mutableListOf())
            Toast.makeText(this, "✅ All transactions cleared!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Failed to clear transactions: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutUser() {
        val prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
        prefs.edit().clear().apply()

        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
