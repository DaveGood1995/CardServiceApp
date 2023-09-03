package com.dgood.paymenthandler.model.request

import com.google.gson.Gson
import com.google.gson.JsonObject

data class RequestOrder(
    val orderId: String,
    val currency: String,
    val totalAmount: Double
)

data class Device(
    val type: String,
    val dataKsn: String,
    val serialNumber: String?
) {
    fun toJson(): String {
        Gson()
        val jsonObject = JsonObject()

        jsonObject.addProperty("type", type)
        jsonObject.addProperty("dataKsn", dataKsn)
        jsonObject.addProperty("serialNumber", serialNumber)

        return jsonObject.toString()
    }
}

data class CardDetails(
    val device: Device,
    val encryptedData: String
) {
    fun toJson(): String {
        val gson = Gson()
        val jsonObject = JsonObject()

        jsonObject.add("device", gson.toJsonTree(device.toJson()))
        jsonObject.addProperty("encrypredData", encryptedData)

        return jsonObject.toString()
    }
}

data class RequestCustomerAccount(
    val device: Device?,
    val payloadType: String,
    val tlv: String?,
    val cardDetails: CardDetails?,
    val cardholderName: String?
) {
    fun toJson(): String {
        val gson = Gson()
        val jsonObject = JsonObject()

        jsonObject.add("device", gson.toJsonTree(device!!.toJson()))
        jsonObject.addProperty("payloadType", payloadType)
        jsonObject.addProperty("tlv", tlv)
        jsonObject.add("cardDetails", gson.toJsonTree(cardDetails!!.toJson()))
        jsonObject.addProperty("cardholderName", cardholderName)



        return jsonObject.toString()
    }
}

data class TransactionRequest(
    val channel: String,
    val terminal: String,
    val order: RequestOrder,
    val customerAccount: RequestCustomerAccount
) {
    fun toJson(): String {
        val gson = Gson()
        val jsonObject = JsonObject()

        jsonObject.addProperty("channel", channel)
        jsonObject.addProperty("terminal", terminal)
        jsonObject.add("order", gson.toJsonTree(order))
        jsonObject.add("customerAccount", gson.toJsonTree(customerAccount))

        return jsonObject.toString()
    }
}
