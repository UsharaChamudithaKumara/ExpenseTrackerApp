package com.example.financetracker

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.financetracker.databinding.ActivityTransactionAdapterBinding

class TransactionAdapter(
    private var transactions: MutableList<Transaction>,
    private val onDelete: (Transaction) -> Unit,
    private val onUpdate: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var selectedCurrency: String = "LKR"

    inner class TransactionViewHolder(val binding: ActivityTransactionAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ActivityTransactionAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.itemView.context

        // ✅ Load selected currency
        val prefs = context.getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
        selectedCurrency = prefs.getString("currency", "LKR") ?: "LKR"

        val convertedAmount = CurrencyHelper.convertFromLKR(transaction.amount, selectedCurrency)

        holder.binding.tvTitle.text = transaction.title
        holder.binding.tvCategoryDate.text = "${transaction.category} | ${transaction.date}"
        holder.binding.tvAmount.text = "${CurrencyHelper.getSymbol(selectedCurrency)} %.2f".format(convertedAmount)

        val color = if (transaction.type == "INCOME") {
            Color.parseColor("#2E7D32")
        } else {
            Color.parseColor("#C62828")
        }
        holder.binding.tvAmount.setTextColor(color)

        holder.binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes") { _, _ -> onDelete(transaction) }
                .setNegativeButton("No", null)
                .show()
        }

        holder.binding.btnEdit.setOnClickListener {
            showUpdateDialog(context, transaction)
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newList: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newList)
        notifyDataSetChanged()
    }

    private fun showUpdateDialog(context: Context, transaction: Transaction) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_transaction, null)

        val etTitle = dialogView.findViewById<EditText>(R.id.etUpdateTitle)
        val etAmount = dialogView.findViewById<EditText>(R.id.etUpdateAmount)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerUpdateCategory)
        val rgType = dialogView.findViewById<RadioGroup>(R.id.rgUpdateType)
        val rbIncome = dialogView.findViewById<RadioButton>(R.id.rbUpdateIncome)
        val rbExpense = dialogView.findViewById<RadioButton>(R.id.rbUpdateExpense)
        val tvDate = dialogView.findViewById<TextView>(R.id.tvUpdateDate)

        // Set initial values
        etTitle.setText(transaction.title)
        etAmount.setText(transaction.amount.toString())

        val categories = arrayOf("Food", "Transport", "Utilities", "Entertainment", "Other")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter
        spinnerCategory.setSelection(categories.indexOf(transaction.category))

        if (transaction.type == "INCOME") rbIncome.isChecked = true else rbExpense.isChecked = true
        tvDate.text = transaction.date

        // Date Picker
        tvDate.setOnClickListener {
            val parts = transaction.date.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val day = parts[2].toInt()
            val dpd = android.app.DatePickerDialog(context, { _, y, m, d ->
                tvDate.text = "$y-${m + 1}-$d"
            }, year, month, day)
            dpd.show()
        }

        AlertDialog.Builder(context)
            .setTitle("Update Transaction")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = etTitle.text.toString().trim()
                val newAmount = etAmount.text.toString().toDoubleOrNull()
                val newCategory = spinnerCategory.selectedItem.toString()
                val newType = if (rbIncome.isChecked) "INCOME" else "EXPENSE"
                val newDate = tvDate.text.toString()

                if (newTitle.isNotEmpty() && newAmount != null && newAmount > 0) {
                    transaction.title = newTitle
                    transaction.amount = newAmount
                    transaction.category = newCategory
                    transaction.type = newType
                    transaction.date = newDate

                    onUpdate(transaction)
                } else {
                    Toast.makeText(context, "Please fill valid title and amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
