package com.example.financetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financetracker.databinding.ActivityHistoryBinding
import com.google.android.material.navigation.NavigationBarView

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: TransactionAdapter
    private var transactions = mutableListOf<Transaction>()  // make it a global list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactions = StorageHelper.getTransactions(this).toMutableList()

        adapter = TransactionAdapter(
            transactions,
            onDelete = { transactionToDelete ->
                val currentList = StorageHelper.getTransactions(this).toMutableList()
                currentList.remove(transactionToDelete)
                StorageHelper.saveTransactions(this, currentList)
                adapter.updateData(currentList)
            },
            onUpdate = { updatedTransaction ->
                val currentList = StorageHelper.getTransactions(this).toMutableList()
                val index = currentList.indexOfFirst { it.id == updatedTransaction.id }
                if (index != -1) {
                    currentList[index] = updatedTransaction
                    StorageHelper.saveTransactions(this, currentList)
                    adapter.updateData(currentList)
                }
            }
        )

        binding.recyclerTransactions.layoutManager = LinearLayoutManager(this)
        binding.recyclerTransactions.adapter = adapter

        // ✅ Bottom Navigation Setup
        binding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, Main2Activity::class.java))
                    true
                }
                R.id.nav_transactions -> {
                    // Already here
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

        // ✅ Highlight the current tab
        binding.bottomNavigation.selectedItemId = R.id.nav_transactions
    }

    override fun onResume() {
        super.onResume()
        // 🔥 Reload transactions when returning to page
        transactions = StorageHelper.getTransactions(this).toMutableList()
        adapter.updateData(transactions)
    }
}
