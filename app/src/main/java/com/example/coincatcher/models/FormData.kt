package com.example.coincatcher.models

import com.example.coincatcher.models.validations.ValidationResults
import java.util.Calendar

class FormData(
    private var title:String,
    private var amount: String,
    private var category:String,
    private var date:String,
    private var type:String

) {
    fun validateTitle(): ValidationResults {
        return if (title.isEmpty()) {

            ValidationResults.Empty("Enter the transaction title")

        } else if (!title.matches(Regex("^[A-Za-z ]+$"))) {

            ValidationResults.Invalid("Title must only contain letters")

        } else {

            ValidationResults.Valid
        }
    }

    fun validateAmount(): ValidationResults {
        return if (amount.isBlank()) {
            ValidationResults.Empty("Enter the transaction amount")
        } else {
            val amountValue = amount.toDoubleOrNull()
            if (amountValue == null || amountValue <= 0.0) {
                ValidationResults.Invalid("Amount must be greater than 0")
            } else {
                ValidationResults.Valid
            }
        }
    }


    fun validateCategory(): ValidationResults {
        return if (category.isEmpty() || category == "Select Category") {
            ValidationResults.Empty("Please select a valid category")
        } else {
            ValidationResults.Valid
        }
    }

    fun validateDate(): ValidationResults {
        if (date.trim().isEmpty()) {
            return ValidationResults.Empty("Select the transaction date")
        }

        // Safe to split since DatePicker always gives "yyyy-MM-dd"
        val (year, month, day) = date.split("-").map { it.toInt() }

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        return if (year == currentYear && month == currentMonth) {
            if (day <= currentDay) {
                ValidationResults.Valid
            } else {
                ValidationResults.Invalid("Future dates within the month are not allowed")
            }
        } else {
            ValidationResults.Invalid("Date must be within the current month")
        }
    }



    fun validateType():ValidationResults{
        return if(type.isEmpty())
        {
            ValidationResults.Empty("Select the transaction type")

        }else
        {
            ValidationResults.Valid
        }
    }



}