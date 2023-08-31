package com.dgood.paymenthandler.model
data class TokenInfo(
    val audience: String,
    val boundTo: String,
    val tokenType: String,
    val token: String,
    val expiresIn: Int,
    val enableReceipts: Boolean,
    val enableHypermedia: Boolean,
    val roles: List<String>,
    val allowedTerminals: List<String>
)