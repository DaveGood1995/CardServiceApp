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
    // Create an instance of the OkHttpClient for making HTTP requests
    private val client = OkHttpClient()

    // Initialize various configuration properties from the Android app's resources
    private val baseUrl = context!!.resources.getString(R.string.basePaymentsUrl)
    private val makePaymentEndpoint = context!!.resources.getString(R.string.makePaymentEndpoint)
    private val getBearerTokenEndpoint = context!!.resources.getString(R.string.getBearerTokenEndpoint)
    private val apiKey = context!!.resources.getString(R.string.apiKey)

    // Private function to retrieve a bearer token from the server
    private fun getBearerToken(): String {
        // Create an HTTP request to get the bearer token
        val request = Request.Builder()
            .url("$baseUrl/$getBearerTokenEndpoint")
            .get()
            .addHeader("Authorization", "Basic $apiKey")
            .build()

        // Execute the HTTP request and obtain the response
        val response: Response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        // Parse the response JSON into a TokenInfo object
        val tokenInfo = Gson().fromJson(responseBody, TokenInfo::class.java)

        // Close the response
        response.close()

        // Return the bearer token
        return tokenInfo.token
    }

    // Public function to make a payment using a TransactionRequest object
    fun makePayment(transactionRequest: TransactionRequest): TransactionResponse {
        try {
            // Retrieve the bearer token
            val token = getBearerToken()

            // Convert the TransactionRequest object to JSON
            val json = transactionRequest.toJson()

            // Define the JSON media type
            val mediaType = "application/json".toMediaType()

            // Create an HTTP request to make a payment
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$baseUrl/$makePaymentEndpoint")  // Construct the URL
                .post(requestBody)                     // Use the JSON request body
                .addHeader("Authorization", "Bearer $token")  // Add the Bearer token header
                .build()

            // Execute the HTTP request and obtain the response
            val response: Response = client.newCall(request).execute()

            // Check if the response is successful
            if (!response.isSuccessful) {
                val responseBody = response.body?.string()
                // Handle different HTTP error codes with custom exceptions
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

            // Parse the response JSON into a TransactionResponse object
            val responseBody = response.body?.string()
            val transactionResponse = Gson().fromJson(responseBody, TransactionResponse::class.java)

            // Close the response
            response.close()

            // Return the transaction response
            return transactionResponse
        } catch (e: IOException) {
            // Handle network-related errors
            throw CustomNetworkException("Network error occurred: ${e.message}")
        } catch (e: JsonSyntaxException) {
            // Handle JSON parsing errors
            throw CustomJsonParseException("JSON parsing error occurred: ${e.message}")
        }
    }
}