package com.example.coincatcher

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coincatcher.models.FormData
import com.example.coincatcher.models.validations.ValidationResults
import java.util.Calendar
import java.util.UUID

class TransactionAdd : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var dateInput: EditText
    private lateinit var incomeRadio: RadioButton
    private lateinit var expenseRadio: RadioButton
    private lateinit var saveButton: Button
    private lateinit var spinnerErrorText: TextView

    private lateinit var transactionManager: TransactionManager

    // Set the budget

    private var count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_add)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Views
        titleInput = findViewById(R.id.editTitle)
        amountInput = findViewById(R.id.editAmount)
        categorySpinner = findViewById(R.id.spinnerCategory)
        dateInput = findViewById(R.id.editDate)
        incomeRadio = findViewById(R.id.radioIncome)
        expenseRadio = findViewById(R.id.radioExpense)
        saveButton = findViewById(R.id.btnSave)
        spinnerErrorText = findViewById(R.id.spinnerErrorText)


        transactionManager = TransactionManager(this)


        // Date Picker for date input
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                dateInput.setText(formattedDate)
                dateInput.error = null

            }, year, month, day)

            datePicker.show()
        }


        //save transaction

        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val amountText = amountInput.text.toString()
            val amount = amountText.toDoubleOrNull()
            val category = categorySpinner.selectedItem.toString()
            val date = dateInput.text.toString()
            val type = if (incomeRadio.isChecked) "Income" else if (expenseRadio.isChecked) "Expense" else ""

            val myForm = FormData(title, amountText, category, date, type)

            val titlevalidation = myForm.validateTitle()
            val amountValidation = myForm.validateAmount()
            val categoryValidation = myForm.validateCategory()
            val dateValidation = myForm.validateDate()
            val typeValidation = myForm.validateType()

            count = 0 // Reset count before validation begins

            when (titlevalidation) {
                is ValidationResults.Valid -> count++
                is ValidationResults.Invalid -> {
                    titleInput.error = titlevalidation.errorMessage
                }
                is ValidationResults.Empty -> {
                    titleInput.error = titlevalidation.errorMessage
                }
            }

            when (amountValidation) {
                is ValidationResults.Valid -> count++
                is ValidationResults.Invalid ->{
                    amountInput.error = amountValidation.errorMessage
                }
                is ValidationResults.Empty -> {
                    amountInput.error = amountValidation.errorMessage
                }
            }

            when (categoryValidation) {
                is ValidationResults.Valid -> {
                    count++
                    spinnerErrorText.visibility = View.GONE
                }
                is ValidationResults.Empty -> {
                    spinnerErrorText.error = categoryValidation.errorMessage
                    spinnerErrorText.visibility = View.VISIBLE
                }
                else -> {}
            }


            when (dateValidation) {
                is ValidationResults.Valid -> count++
                is ValidationResults.Invalid -> {
                    dateInput.error = dateValidation.errorMessage
                }
                is ValidationResults.Empty -> {
                    dateInput.error = dateValidation.errorMessage
                }
            }

            when (typeValidation) {
                is ValidationResults.Valid -> count++
                is ValidationResults.Empty -> {
                    incomeRadio.error = typeValidation.errorMessage
                }
                else -> {}
            }

            if (count == 5 && amount != null) {
                displayAlert("Success", "You have successfully added the transaction")

                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    type = type
                )
                transactionManager.addTransaction(transaction, this)
                Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show()
            } else {
                count = 0
            }
        }


        incomeRadio.setOnClickListener {
            incomeRadio.error = null
            expenseRadio.error = null
        }

        expenseRadio.setOnClickListener {
            expenseRadio.error = null
            incomeRadio.error = null
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

        val settingsButton: ImageButton = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


    }


    fun displayAlert(title:String,message:String)
    {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok"){dialog, which->

            finish()
        }

        val dialog = builder.create()
        dialog.show()

    }




}