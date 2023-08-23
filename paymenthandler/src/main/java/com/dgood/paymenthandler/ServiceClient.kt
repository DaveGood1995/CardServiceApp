package com.dgood.paymenthandler

import android.content.Context
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ServiceClient(context: Context) {
    private val client = OkHttpClient()
    private val baseUrl = context.resources.getString(R.string.basePaymentsUrl)
    private val makePaymentEndpoint = context.resources.getString(R.string.makePaymentEndpoint)
    private val apiKey = context.resources.getString(R.string.apiKey)

    fun makePayment(endpoint: String, payload: String): Boolean {
        val mediaType = "application/json".toMediaType()
        val requestBody = payload.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/$makePaymentEndpoint")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        val response = client.newCall(request).execute()
        return response.isSuccessful
    }
}