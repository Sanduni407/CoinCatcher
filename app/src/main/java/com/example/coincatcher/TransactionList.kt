package com.example.coincatcher

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TransactionList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var transactionManager: TransactionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        transactionManager = TransactionManager(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        adapter = TransactionAdapter(
            transactionManager.getTransactions(),
            onEditClick = { transaction ->
                val intent = Intent(this, EditTransactionActivity::class.java)
                intent.putExtra("transaction_id", transaction.id)
                startActivity(intent)
            },
            onDeleteClick = { transaction ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete") { _, _ ->
                        transactionManager.deleteTransaction(transaction.id)
                        adapter.updateList(transactionManager.getTransactions())
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },this
        )

        recyclerView.adapter = adapter



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

    override fun onResume() {
        super.onResume()
        adapter.updateList(transactionManager.getTransactions())
    }
}