package com.dgood.paymenthandler

import android.content.Context
import com.dgood.paymenthandler.model.TokenInfo
import com.dgood.paymenthandler.model.request.TransactionRequest
import com.dgood.paymenthandler.model.response.TransactionResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class ServiceClient(context: Context?) {
    private val client = OkHttpClient()
    private val baseUrl = context!!.resources.getString(R.string.basePaymentsUrl)
    private val makePaymentEndpoint = context!!.resources.getString(R.string.makePaymentEndpoint)
    private val getBearerTokenEndpoint = context!!.resources.getString(R.string.getBearerTokenEndpoint)
    private val apiKey = context!!.resources.getString(R.string.apiKey)

    private fun getBearerToken(): String {

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
        try {
            val token = getBearerToken()
            val json = transactionRequest.toJson()
            val mediaType = "application/json".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$baseUrl/$makePaymentEndpoint")  // Construct the URL
                .post(requestBody)                     // Use the JSON request body
                .addHeader("Authorization", "Bearer $token")  // Add the Bearer token header
                .build()

            val response: Response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                val responseBody = response.body?.string()
                when (response.code) {
                    400 -> throw BadRequestException("Bad Request: $responseBody")
                    401 -> throw UnauthorizedException("Unauthorized: $responseBody")
                    403 -> throw ForbiddenException("Forbidden: $responseBody")
                    404 -> throw NotFoundException("Not Found: $responseBody")
                    422 -> throw UnprocessableEntityException("Unprocessable Entity: $responseBody")
                    500 -> throw InternalServerErrorException("Internal Server Error: $responseBody")
                    501 -> throw NotImplementedException("Not Implemented: $responseBody")
                    else -> throw IOException("HTTP Error: ${response.code}")
                }
            }

            val responseBody = response.body?.string()
            val transactionResponse = Gson().fromJson(responseBody, TransactionResponse::class.java)
            response.close()

            return transactionResponse
        } catch (e: IOException) {
            throw CustomNetworkException("Network error occurred: ${e.message}")
        } catch (e: JsonSyntaxException) {
            throw CustomJsonParseException("JSON parsing error occurred: ${e.message}")
        }
    }
}
