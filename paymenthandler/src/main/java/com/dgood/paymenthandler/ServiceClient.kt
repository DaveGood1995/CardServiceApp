package com.dgood.paymenthandler

import android.content.Context
import com.dgood.paymenthandler.model.request.TransactionRequest
import com.dgood.paymenthandler.model.response.TransactionResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ServiceClient(context: Context) {
    private val client = OkHttpClient()
    private val baseUrl = context.resources.getString(R.string.basePaymentsUrl)
    private val makePaymentEndpoint = context.resources.getString(R.string.makePaymentEndpoint)
    private val apiKey = context.resources.getString(R.string.apiKey)

    fun makePayment(transactionRequest: TransactionRequest): TransactionResponse {
        val json = transactionRequest.toJson()
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/$makePaymentEndpoint")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        val response: Response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        val transactionResponse = Gson().fromJson(responseBody, TransactionResponse::class.java)

        response.close()

        return transactionResponse

    }
}
