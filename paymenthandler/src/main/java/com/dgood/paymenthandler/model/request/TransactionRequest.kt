package com.dgood.paymenthandler.model.request

import com.google.gson.Gson

data class Order(
    val orderId: String,
    val currency: String,
    val totalAmount: Double
)

data class Device(
    val type: String,
    val dataKsn: String
)

data class CustomerAccount(
    val device: Device,
    val tlv: String,
    val payloadType: String
)

data class TransactionRequest(
    val channel: String,
    val terminal: String,
    val order: Order,
    val customerAccount: CustomerAccount
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

