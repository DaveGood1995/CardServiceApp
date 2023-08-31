package com.dgood.paymenthandler

import android.content.Context
import com.dgood.paymenthandler.model.TokenInfo
import com.dgood.paymenthandler.model.request.TransactionRequest
import com.dgood.paymenthandler.model.response.TransactionResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ServiceClient(context: Context?) {
    private val client = OkHttpClient()
    private val baseUrl = context!!.resources.getString(R.string.basePaymentsUrl)
    private val makePaymentEndpoint = context!!.resources.getString(R.string.makePaymentEndpoint)
    private val getBearerTokenEndpoint = context!!.resources.getString(R.string.getBearerTokenEndpoint)
    private val apiKey = context!!.resources.getString(R.string.apiKey)


    private fun getBearerToken(): String? {

        val request = Request.Builder()
            .url("$baseUrl/$getBearerTokenEndpoint")
            .get()
            .addHeader("Authorization", "Basic $apiKey")
            .build()

        val response: Response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        val tokenInfo = Gson().fromJson(responseBody, TokenInfo::class.java)

        response.close()

        return tokenInfo.token

    }
    fun makePayment(transactionRequest: TransactionRequest): TransactionResponse {
        val token = getBearerToken()
        val json = transactionRequest.toJson()
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/$makePaymentEndpoint")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response: Response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        val transactionResponse = Gson().fromJson(responseBody, TransactionResponse::class.java)

        response.close()

        return transactionResponse
    }
}
