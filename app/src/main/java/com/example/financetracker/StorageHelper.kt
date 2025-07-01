package com.example.financetracker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object StorageHelper {
    private const val PREF_NAME = "FinanceApp"
    private const val KEY_TRANSACTIONS = "transactions"

    val gson = Gson()
    val transactionListType = object : TypeToken<MutableList<Transaction>>() {}.type

    // Save transactions to SharedPreferences
    fun saveTransactions(context: Context, transactions: MutableList<Transaction>) {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(transactions)
        sharedPrefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Retrieve transactions from SharedPreferences
    fun getTransactions(context: Context): MutableList<Transaction> {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = sharedPrefs.getString(KEY_TRANSACTIONS, null)
        return if (json != null) {
            gson.fromJson(json, transactionListType)
        } else {
            mutableListOf()
        }
    }

    // Save transactions to a backup file
    fun backupTransactions(context: Context, transactions: MutableList<Transaction>) {
        val file = File(context.filesDir, "backup.json")
        val json = gson.toJson(transactions)
        file.writeText(json)
    }

    // Load transactions from backup file
    fun loadBackupTransactions(context: Context): MutableList<Transaction>? {
        val file = File(context.filesDir, "backup.json")
        return if (file.exists()) {
            val json = file.readText()
            gson.fromJson(json, transactionListType)
        } else {
            null
        }
    }
}
