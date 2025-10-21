package com.example.coincatcher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTextView)
        val amount: TextView = itemView.findViewById(R.id.amountTextView)
        val category: TextView = itemView.findViewById(R.id.categoryTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val typeBadge: TextView = itemView.findViewById(R.id.typeTextView)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        val sharedPref = context.getSharedPreferences("coinCatcherPrefs", Context.MODE_PRIVATE)
        val currency = sharedPref.getString("user_currency", "Rs.")

        holder.title.text = transaction.title
        holder.amount.text = "$currency ${transaction.amount}"
        holder.category.text = transaction.category
        holder.date.text = transaction.date
        holder.typeBadge.text = transaction.type


        val isExpense = transaction.type.equals("Expense", ignoreCase = true)

        val colorBadge = if (isExpense) 0xFFB12C23.toInt() else 0xFF009688.toInt()
        val colorText = if (isExpense) 0xFFD32F2F.toInt() else 0xFF388E3C.toInt()

        holder.typeBadge.setBackgroundColor(colorBadge)
        holder.amount.setTextColor(colorText)

        holder.editButton.setOnClickListener { onEditClick(transaction) }
        holder.deleteButton.setOnClickListener { onDeleteClick(transaction) }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateList(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
