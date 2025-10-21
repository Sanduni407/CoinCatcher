package com.example.coincatcher

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coincatcher.models.FormData
import com.example.coincatcher.models.validations.ValidationResults
import java.util.Calendar

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var typeGroup: RadioGroup
    private lateinit var radioIncome: RadioButton
    private lateinit var radioExpense: RadioButton
    private lateinit var dateInput: EditText
    private lateinit var spinnerErrorText: TextView

    private lateinit var transactionManager: TransactionManager
    private lateinit var transaction: Transaction
    private lateinit var categories: List<String>

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_transaction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        titleInput = findViewById(R.id.editTitle)
        amountInput = findViewById(R.id.editAmount)
        categorySpinner = findViewById(R.id.editCategory)
        typeGroup = findViewById(R.id.editTypeGroup)
        radioIncome = findViewById(R.id.radioIncome)
        radioExpense = findViewById(R.id.radioExpense)
        dateInput = findViewById(R.id.editDate)
        spinnerErrorText = findViewById(R.id.spinnerErrorText)

        transactionManager = TransactionManager(this)



        categories = listOf("Select Category","Food and Groceries", "Transportation", "Shopping", "Utilities", "Entertainment", "Health", "Salary", "Other Income", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = spinnerAdapter



        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                val selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                dateInput.setText(selectedDate)
            }, year, month, day).show()
        }


        // Load transaction
        val transactionId = intent.getStringExtra("transaction_id") ?: return
        val transactions = transactionManager.getTransactions()
        transaction = transactions.find { it.id == transactionId } ?: return


        titleInput.setText(transaction.title)
        amountInput.setText(transaction.amount.toString())
        dateInput.setText(transaction.date)

        // Set category in spinner
        val categoryIndex = categories.indexOf(transaction.category)
        if (categoryIndex >= 0) {
            categorySpinner.setSelection(categoryIndex)
        }



        if (transaction.type == "Income") {
            radioIncome.isChecked = true
        } else {
            radioExpense.isChecked = true
        }



        findViewById<Button>(R.id.buttonUpdate).setOnClickListener {
            val newTitle = titleInput.text.toString()
            val amountText = amountInput.text.toString()
            val newCategory = categorySpinner.selectedItem.toString()
            val newDate = dateInput.text.toString()
            val newType = if (radioIncome.isChecked) "Income" else if (radioExpense.isChecked) "Expense" else ""

            val formData = FormData(newTitle, amountText, newCategory, newDate, newType)


            titleInput.error = null
            amountInput.error = null
            dateInput.error = null
            spinnerErrorText.error = null
            radioIncome.error = null
            radioExpense.error = null


            val titleValidation = formData.validateTitle()
            val amountValidation = formData.validateAmount()
            val categoryValidation = formData.validateCategory()
            val dateValidation = formData.validateDate()
            val typeValidation = formData.validateType()

            var validCount = 0

            when (titleValidation) {
                is ValidationResults.Valid -> validCount++
                is ValidationResults.Empty->titleInput.error = titleValidation.errorMessage
                is ValidationResults.Invalid -> titleInput.error = titleValidation.errorMessage
            }

            when (amountValidation) {
                is ValidationResults.Valid -> validCount++
                is ValidationResults.Empty -> amountInput.error = amountValidation.errorMessage
                is ValidationResults.Invalid -> amountInput.error = amountValidation.errorMessage
            }

            when (categoryValidation) {
                is ValidationResults.Valid -> {
                    validCount++
                    spinnerErrorText.visibility = View.GONE
                }
                is ValidationResults.Empty -> {
                    spinnerErrorText.error = categoryValidation.errorMessage
                    spinnerErrorText.visibility = View.VISIBLE
                }

                else ->{}
            }

            when (dateValidation) {
                is ValidationResults.Valid -> validCount++
                is ValidationResults.Empty -> dateInput.error = dateValidation.errorMessage
                is ValidationResults.Invalid -> dateInput.error = dateValidation.errorMessage
            }

            when (typeValidation) {
                is ValidationResults.Valid -> validCount++
                is ValidationResults.Empty -> {
                    radioIncome.error = typeValidation.errorMessage
                    radioExpense.error = typeValidation.errorMessage
                }
                else->{}
            }

            if (validCount == 5) {
                val newAmount = amountText.toDoubleOrNull() ?: 0.0

                transaction.title = newTitle
                transaction.amount = newAmount
                transaction.category = newCategory
                transaction.type = newType
                transaction.date = newDate

                transactionManager.updateTransaction(transaction)

                Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }



        findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            finish()
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

        val homepage: ImageButton = findViewById(R.id.btnHome)
        homepage.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }

        val settingsButton:ImageButton = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


    }
}