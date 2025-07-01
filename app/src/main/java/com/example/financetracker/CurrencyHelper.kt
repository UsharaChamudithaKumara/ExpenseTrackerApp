package com.example.financetracker

object CurrencyHelper {

    private val rates = mapOf(
        "LKR" to 1.0,
        "USD" to 0.0033,
        "EUR" to 0.0030,
        "GBP" to 0.0026,
        "AUD" to 0.0050
    )

    fun convertFromLKR(amountLKR: Double, currency: String): Double {
        return (rates[currency] ?: 1.0) * amountLKR
    }

    fun getSymbol(currency: String): String {
        return when (currency) {
            "LKR" -> "LKR"
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "AUD" -> "A$"
            else -> "LKR"
        }
    }
}
