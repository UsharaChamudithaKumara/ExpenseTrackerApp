package com.example.financetracker

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.financetracker.databinding.ActivityTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionBinding
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categories = arrayOf("Food", "Transport", "Utilities", "Entertainment", "Other")
        binding.spinnerCategory.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        binding.tvDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val amountText = binding.etAmount.text.toString().trim()
            val category = binding.spinnerCategory.selectedItem.toString()
            val type = if (binding.rbIncome.isChecked) "INCOME" else "EXPENSE"

            if (title.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Enter a valid positive amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val transactionDate = if (selectedDate.isNotEmpty()) selectedDate else today

            if (transactionDate > today) {
                Toast.makeText(this, "Cannot select a future date!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transactions = StorageHelper.getTransactions(this).toMutableList()

            // Budget checking (only for expenses)
            if (type == "EXPENSE") {
                val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
                val budget = prefs.getFloat("monthly_budget", 0f).toDouble()
                val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

                // ⚡ Only show notification if exceeded
                if ((totalExpense + amount) > budget) {
                    NotificationHelper.showBudgetExceededNotification(this)
                    Toast.makeText(this, "⚠️ Budget exceeded!", Toast.LENGTH_LONG).show()
                    // ✅ But allow saving
                }
            }

            // Generate ID
            val id = (transactions.maxByOrNull { it.id }?.id ?: 0) + 1

            // Add new transaction
            transactions.add(Transaction(id, title, category, transactionDate, amount, type))

            StorageHelper.saveTransactions(this, transactions)

            Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val dpd = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(Calendar.YEAR, year)
                selectedCal.set(Calendar.MONTH, month)
                selectedCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val today = Calendar.getInstance()

                if (selectedCal.after(today)) {
                    Toast.makeText(this, "Cannot select a future date!", Toast.LENGTH_SHORT).show()
                } else {
                    selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCal.time)
                    binding.tvDate.text = selectedDate
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dpd.show()
    }
}
