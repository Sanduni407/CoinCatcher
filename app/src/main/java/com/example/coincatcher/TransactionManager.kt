package com.example.coincatcher

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class TransactionManager(private val context: Context) {
    private val PREF_NAME = "FinanceApp"
    private val KEY_TRANSACTIONS = "transactions"
    private val budgetManager = BudgetManager(context)

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getTransactions(): MutableList<Transaction> {
        val json = getPrefs().getString(KEY_TRANSACTIONS, "[]")
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        return Gson().fromJson(json, type)
    }



    fun addTransaction(transaction: Transaction, activity: TransactionAdd) {
        val transactions = getTransactions()
        transactions.add(transaction)


        if (transaction.type == "Expense") {
            budgetManager.decreaseBudget(transaction.amount)

        }

        saveTransactions(transactions)
    }



    fun updateTransaction(updatedTransaction: Transaction) {
        val transactions = getTransactions()
        val index = transactions.indexOfFirst { it.id == updatedTransaction.id }
        if (index != -1) {
            val oldTransaction = transactions[index]

            // Handle budget changes for expense
            if (oldTransaction.type == "Expense") {
                budgetManager.increaseBudget(oldTransaction.amount)
            }
            if (updatedTransaction.type == "Expense") {
                budgetManager.decreaseBudget(updatedTransaction.amount)
            }

            transactions[index] = updatedTransaction
            saveTransactions(transactions)
        }
    }

    fun deleteTransaction(transactionId: String) {
        val transactions = getTransactions()
        val transactionToDelete = transactions.find { it.id == transactionId }

        // Restore the budget before deletion if it's an expense
        if (transactionToDelete != null && transactionToDelete.type == "Expense") {
            budgetManager.increaseBudget(transactionToDelete.amount)
        }

        val updatedList = transactions.filter { it.id != transactionId }
        saveTransactions(updatedList)
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        val json = Gson().toJson(transactions)
        getPrefs().edit().putString(KEY_TRANSACTIONS, json).apply()
    }


    fun getExpenseSummaryByCategory(): Map<String, Double> {
        val summary = mutableMapOf<String, Double>()
        val currentMonth = LocalDate.now().monthValue
        val currentYear = LocalDate.now().year

        for (transaction in getTransactions()) {
            if (transaction.type.equals("Expense", ignoreCase = true)) {
                val transactionDate = LocalDate.parse(transaction.date)
                if (transactionDate.monthValue == currentMonth && transactionDate.year == currentYear) {
                    summary[transaction.category] = summary.getOrDefault(transaction.category, 0.0) + transaction.amount
                }
            }
        }
        return summary
    }


    fun getTotalIncome(): Double {
        return getTransactions().filter { it.type == "Income" }
            .sumOf { it.amount }
    }

    fun getTotalExpense(): Double {
        return getTransactions().filter { it.type == "Expense" }
            .sumOf { it.amount }
    }


}
