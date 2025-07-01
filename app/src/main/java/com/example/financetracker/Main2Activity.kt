package com.example.financetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.financetracker.databinding.ActivityMain2Binding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationBarView

class Main2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private var transactions = mutableListOf<Transaction>()
    private var monthlyBudget = 50000.0
    private var selectedCurrency = "LKR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        binding.tvWelcome.text = "Hello, $username"

        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_transactions -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_budget -> {
                    startActivity(Intent(this, BudgetActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        })

        binding.fabAddTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("fintrack_prefs", MODE_PRIVATE)
        selectedCurrency = prefs.getString("currency", "LKR") ?: "LKR"
        transactions = StorageHelper.getTransactions(this)
        updateUI()
    }

    private fun updateUI() {
        val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        // Convert
        val convertedBalance = CurrencyHelper.convertFromLKR(balance, selectedCurrency)
        val convertedIncome = CurrencyHelper.convertFromLKR(totalIncome, selectedCurrency)
        val convertedExpense = CurrencyHelper.convertFromLKR(totalExpense, selectedCurrency)

        binding.tvBalance.text = "Balance: ${CurrencyHelper.getSymbol(selectedCurrency)} %.2f".format(convertedBalance)
        binding.tvIncome.text = "Income: ${CurrencyHelper.getSymbol(selectedCurrency)} %.2f".format(convertedIncome)
        binding.tvExpense.text = "Expense: ${CurrencyHelper.getSymbol(selectedCurrency)} %.2f".format(convertedExpense)

        setPieChart(convertedIncome, convertedExpense)
    }

    private fun setPieChart(income: Double, expense: Double) {
        val entries = ArrayList<PieEntry>()
        if (income > 0) entries.add(PieEntry(income.toFloat(), "Income"))
        if (expense > 0) entries.add(PieEntry(expense.toFloat(), "Expense"))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.centerText = "Income vs Expense"
        binding.pieChart.animateY(1000)
        binding.pieChart.invalidate()
    }
}
