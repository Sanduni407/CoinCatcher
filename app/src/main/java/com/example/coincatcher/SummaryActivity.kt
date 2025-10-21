package com.example.coincatcher

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class SummaryActivity : AppCompatActivity() {

    private lateinit var transactionManager: TransactionManager
    private lateinit var summaryText: TextView
    private lateinit var pieChart: PieChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        summaryText = findViewById(R.id.textSummary)
        pieChart = findViewById(R.id.pieChart)
        transactionManager = TransactionManager(this)

        showSummary()


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

    private fun showSummary() {

        val sharedPref = getSharedPreferences("coinCatcherPrefs", MODE_PRIVATE)
        val currency = sharedPref.getString("user_currency", "USD")

        val summary = transactionManager.getExpenseSummaryByCategory()
        val summaryString = summary.entries.joinToString("\n") { "${it.key}: $currency ${String.format("%.2f", it.value)}" }
        summaryText.text = summaryString

        val entries = summary.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "")

        val customColors = listOf(
            Color.parseColor("#FF6F61"),
            Color.parseColor("#6B5B95"),
            Color.parseColor("#88B04B"),
            Color.parseColor("#F7CAC9"),
            Color.parseColor("#92A8D1")
        )
        dataSet.colors = customColors


        dataSet.colors = customColors

        dataSet.valueTextSize = 16f
        dataSet.valueTextColor = android.graphics.Color.BLACK

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.setEntryLabelColor(android.graphics.Color.DKGRAY)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.description.isEnabled = false
        pieChart.centerText = "Expenses"
        pieChart.setCenterTextSize(20f)


        val legend = pieChart.legend
        legend.isEnabled = true
        legend.textSize = 16f
        legend.formSize = 14f
        legend.formToTextSpace = 10f
        legend.xEntrySpace = 20f
        legend.yEntrySpace = 10f
        legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
        legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER

        pieChart.animateY(1000)
        pieChart.invalidate()
    }

}