package com.example.coincatcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsActivity : AppCompatActivity() {

    private lateinit var budgetInput: EditText
    private lateinit var budgetManager: BudgetManager




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        budgetManager = BudgetManager(this)

        budgetInput = findViewById(R.id.budgetInput)
        val saveButton: Button = findViewById(R.id.saveBudgetButton)

        saveButton.setOnClickListener {
            val input = budgetInput.text.toString()
            if (input.isNotEmpty()) {
                val newBudget = input.toDouble()
                budgetManager.setBudget(newBudget)
                Toast.makeText(this, "Budget updated!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }


        val currencyInput: EditText = findViewById(R.id.tvcurrency)
        val saveCurrencyButton: Button = findViewById(R.id.currencybtn)


        saveCurrencyButton.setOnClickListener{

            val currency = currencyInput.text.toString()
            if (currency.isNotEmpty()) {
                val sharedPref = getSharedPreferences("coinCatcherPrefs", MODE_PRIVATE)
                sharedPref.edit().putString("user_currency", currency).apply()
                Toast.makeText(this, "Currency updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a currency", Toast.LENGTH_SHORT).show()
            }
        }


        //implement restore and backup functionality

        val backupButton: Button = findViewById(R.id.btnBackup)
        val restoreButton: Button = findViewById(R.id.btnRestore)

        val transactionManager = TransactionManager(this)
        val backupFileName = "transaction_backup.json"

        backupButton.setOnClickListener {
            val transactions = transactionManager.getTransactions()
            val json = Gson().toJson(transactions)

            try {
                openFileOutput(backupFileName, Context.MODE_PRIVATE).use {
                    it.write(json.toByteArray())
                }
                Toast.makeText(this, "Backup successful!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Backup failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        restoreButton.setOnClickListener {
            try {
                val json = openFileInput(backupFileName).bufferedReader().use { it.readText() }
                val type = object : TypeToken<MutableList<Transaction>>() {}.type
                val restoredTransactions: MutableList<Transaction> = Gson().fromJson(json, type)

                // Get current transactions
                val currentTransactions = transactionManager.getTransactions()
                val currentIds = currentTransactions.map { it.id }.toSet()


                val newTransactions = restoredTransactions.filter { it.id !in currentIds }

                if (newTransactions.isNotEmpty()) {
                    val allTransactions = currentTransactions + newTransactions
                    val prefs = getSharedPreferences("FinanceApp", Context.MODE_PRIVATE)
                    prefs.edit().putString("transactions", Gson().toJson(allTransactions)).apply()

                    recalculateBudgetFromTransactions(newTransactions)
                    Toast.makeText(this, "${newTransactions.size} new transaction(s) restored!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No new transactions to restore.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Restore failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }




        //create navigations

        val viewTransaction: ImageButton = findViewById(R.id.viewTransaction)

        viewTransaction.setOnClickListener {
            val intent = Intent(this, TransactionList::class.java)
            startActivity(intent)
        }


        // View summary page
        val viewSummaryButton: ImageButton = findViewById(R.id.btnViewSummary)
        viewSummaryButton.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        val homepage:ImageButton = findViewById(R.id.btnHome)
        homepage.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }

    }

    fun recalculateBudgetFromTransactions(transactions: List<Transaction>) {
        var budget = budgetManager.getBudget()


        for (t in transactions) {
            if (t.type == "Expense") {
                budget -= t.amount
            }
        }
        budgetManager.setBudget(budget)
    }

}