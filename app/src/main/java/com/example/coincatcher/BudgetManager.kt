package com.example.coincatcher

import android.content.Context

class BudgetManager(context: Context) {

    private val prefs = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)



    companion object {
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
    }

    fun getBudget(): Double {
        return prefs.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
    }

    fun setBudget(value: Double) {
        prefs.edit().putFloat(KEY_MONTHLY_BUDGET, value.toFloat()).apply()
    }

    fun increaseBudget(amount: Double) {
        setBudget(getBudget() + amount)
    }

    fun decreaseBudget(amount: Double) {
        setBudget(getBudget() - amount)
    }


    fun isBudgetLow(): Boolean {
        return getBudget() <= 500

    }


}
