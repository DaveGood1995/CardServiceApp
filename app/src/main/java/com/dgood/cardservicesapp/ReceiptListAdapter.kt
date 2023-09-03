package com.dgood.cardservicesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dgood.paymenthandler.model.response.Receipt

class ReceiptListAdapter(private val receiptData: List<Pair<Long, Receipt>>, private val onItemClick: (Long) -> Unit) :
    RecyclerView.Adapter<ReceiptListAdapter.ReceiptViewHolder>() {

    inner class ReceiptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTextView: TextView = itemView.findViewById(R.id.text_order_number)
        val timestampTextView: TextView = itemView.findViewById(R.id.text_timestamp)
        val amountTextView: TextView = itemView.findViewById(R.id.text_amount)
        val currencyTextView: TextView = itemView.findViewById(R.id.text_currency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.receipt_item, parent, false)
        return ReceiptViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val (databaseId, currentReceipt) = receiptData[position]

        holder.orderIdTextView.text = currentReceipt.orderId
        holder.timestampTextView.text = currentReceipt.timestamp
        holder.amountTextView.text = currentReceipt.amount.toString()
        holder.currencyTextView.text = currentReceipt.currency

        holder.itemView.setOnClickListener {
            onItemClick(databaseId)
        }
    }

    override fun getItemCount() = receiptData.size
}