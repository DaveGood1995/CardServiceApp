package com.dgood.paymenthandler

import com.dgood.paymenthandler.model.response.Receipt
import com.dgood.paymenthandler.model.response.TransactionResponse
class ReceiptManager {
    private val storedReceipts = mutableListOf<Receipt>()

    fun generateReceipt(transactionResponse: TransactionResponse): Receipt {
        val formattedReceipt = buildFormattedReceipt(transactionResponse)
        val receipt = Receipt(formattedReceipt, transactionResponse)
        storedReceipts.add(receipt)
        return receipt
    }

    fun getStoredReceipts(): List<Receipt> {
        return storedReceipts.toList()
    }

    private fun buildFormattedReceipt(response: TransactionResponse): String {
        // Create a formatted receipt string based on the transaction response data
        // You can customize this format based on your needs
        return "Receipt for transaction: ${response.uniqueReference}\nTotal Amount: ${response.order.totalAmount}"
    }
}
