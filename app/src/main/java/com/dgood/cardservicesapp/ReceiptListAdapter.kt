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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.receipt_item, parent, false)
        return ReceiptViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val (databaseId, currentReceipt) = receiptData[position]

        val context = holder.itemView.context
        val orderTemplate = context.resources.getString(R.string.order_number_template)
        val timeTemplate = context.resources.getString(R.string.datetime_template)
        val amountTemplate = context.resources.getString(R.string.amount_template)

        holder.orderIdTextView.text = String.format(orderTemplate, currentReceipt.orderId)
        holder.timestampTextView.text = String.format(timeTemplate, currentReceipt.timestamp)
        holder.amountTextView.text = String.format(amountTemplate,currentReceipt.amount.toString())

        holder.itemView.setOnClickListener {
            onItemClick(databaseId)
        }
    }

    override fun getItemCount() = receiptData.size
}