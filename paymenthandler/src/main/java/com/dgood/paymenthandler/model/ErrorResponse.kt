package com.dgood.paymenthandler.model

data class ErrorResponse(
    val debugIdentifier: String,
    val details: List<ErrorDetail>
)

data class ErrorDetail(
    val errorCode: String,
    val errorMessage: String,
    val source: ErrorSource
)

data class ErrorSource(
    val location: String,
    val property: String
)