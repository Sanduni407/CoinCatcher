package com.example.coincatcher

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar
import java.util.Locale

class Home : AppCompatActivity() {



    private lateinit var budgetTextView: TextView
    private lateinit var budgetManager: BudgetManager
    private lateinit var transactionManager: TransactionManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textView: TextView = findViewById(R.id.dateTV)

        val calendar = Calendar.getInstance()


        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val currentMonth = monthFormat.format(calendar.time)
        val currentYear = calendar.get(Calendar.YEAR)


        val startDate = "01 $currentMonth $currentYear"


        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val endDate = "$lastDay $currentMonth $currentYear"


        val dateRange = "$startDate - $endDate"


        textView.text = dateRange





        val transaction_add:ImageView = findViewById(R.id.transaction_add)

        transaction_add.setOnClickListener {
            val intent = Intent(this, TransactionAdd::class.java)
            startActivity(intent)
        }

        val viewTransaction:ImageButton = findViewById(R.id.viewTransaction)

        viewTransaction.setOnClickListener {
            val intent = Intent(this, TransactionList::class.java)
            startActivity(intent)
        }

         // navigate to the settings
        val settingsButton:ImageButton = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


        budgetManager = BudgetManager(this)
        budgetTextView = findViewById(R.id.budgetTextView)


        // View summary page
        val viewSummaryButton: ImageButton = findViewById(R.id.btnViewSummary)
        viewSummaryButton.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        //make notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "budget_channel",
                "Budget Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when budget is zero or negative"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101)
            }
        }

        transactionManager = TransactionManager(this)




    }

    override fun onResume() {
        super.onResume()
        updateBudgetUI()
        updateBudgetDisplay()
    }

    private fun updateBudgetDisplay() {

        val sharedPref = getSharedPreferences("coinCatcherPrefs", MODE_PRIVATE)
        val currency = sharedPref.getString("user_currency", "USD")

        val currentBudget = budgetManager.getBudget()
        budgetTextView.text = "$currency ${String.format("%.2f", currentBudget)}"
    }


    fun updateBudgetUI() {
        val budgetTextView = findViewById<TextView>(R.id.budgetTextView)
        val warningTextView = findViewById<TextView>(R.id.budgetWarningText)
        val incomeTextView = findViewById<TextView>(R.id.incomeview)
        val expenseTextView = findViewById<TextView>(R.id.expenseView)
        val balancetv = findViewById<TextView>(R.id.balanceTV)

        val sharedPref = getSharedPreferences("coinCatcherPrefs", MODE_PRIVATE)
        val currency = sharedPref.getString("user_currency", "USD")

        val currentBudget = budgetManager.getBudget()

         var income = transactionManager.getTotalIncome()
        var expense = transactionManager.getTotalExpense()

        var balance = income - expense



        incomeTextView.text = "$currency ${String.format("%.2f", income)}"
        expenseTextView.text = "$currency -${String.format("%.2f", expense)}"
        balancetv.text = "$currency ${String.format("%.2f", balance)} "




        if (currentBudget <= 0) {
            sendBudgetNotification()
        }

        if (currentBudget <= 500) {
            budgetTextView.setTextColor(getColor(android.R.color.holo_red_dark))
            warningTextView.text = "⚠️ Your budget is getting low. Please be cautious!"
            warningTextView.visibility = View.VISIBLE
        } else {
            // Reset to normal if budget is fine
            budgetTextView.setTextColor(getColor(android.R.color.white))
            warningTextView.visibility = View.GONE
            warningTextView.text = ""
        }



    }


    private fun sendBudgetNotification() {
        val builder = NotificationCompat.Builder(this, "budget_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Budget Alert")
            .setContentText("You have exceeded your monthly budget.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(1001, builder.build())
    }



}