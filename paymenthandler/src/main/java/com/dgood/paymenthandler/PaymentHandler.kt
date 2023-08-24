package com.dgood.paymenthandler

import android.content.Context
import com.dgood.paymenthandler.model.request.CustomerAccount
import com.dgood.paymenthandler.model.request.Device
import com.dgood.paymenthandler.model.request.Order
import com.dgood.paymenthandler.model.request.TransactionRequest
import com.dgood.paymenthandler.model.response.TransactionResponse


class PaymentHandler(private val context: Context) {

    private val serviceClient = ServiceClient(context)

    fun insertCardTransaction(amount: Double, order: String, currency: String): String {
        val message = "Card Inserted: Amount - %s %.2f, Order - %s".format(currency, amount, order)
        return message
    }

    fun makePayment(channel: String, terminal: String, order: Order, device: Device, account: CustomerAccount) {

        val transactionRequest = TransactionRequest("POS", "51234", order, account)
        val response: TransactionResponse = serviceClient.makePayment(transactionRequest)


    }
}