package com.dgood.paymenthandler.model.response

import com.dgood.paymenthandler.model.request.CustomerAccount
import com.dgood.paymenthandler.model.request.Order

data class OrderBreakdown(
    val subtotalAmount: Double
)

data class Order(
    val orderId: String,
    val currency: String,
    val totalAmount: Double,
    val orderBreakdown: OrderBreakdown
)

data class CustomerAccount(
    val cardType: String,
    val cardholderName: String,
    val maskedPan: String,
    val expiryDate: String,
    val entryMethod: String
)

data class SecurityCheck(
    val cvvResult: String,
    val avsResult: String
)

data class StoredPaymentCredentials(
    val terminal: String,
    val merchantReference: String,
    val cardholderName: String,
    val credentialsNumber: String,
    val maskedPan: String,
    val securityCheck: String
)

data class TransactionResult(
    val type: String,
    val status: String,
    val approvalCode: String,
    val dateTime: String,
    val currency: String,
    val authorizedAmount: Double,
    val resultCode: String,
    val resultMessage: String,
    val storedPaymentCredentials: StoredPaymentCredentials
)

data class AdditionalDataField(
    val name: String,
    val value: String
)

data class EmvTag(
    val hex: String,
    val value: String
)

data class TransactionResponse(
    val uniqueReference: String,
    val terminal: String,
    val order: Order,
    val customerAccount: CustomerAccount,
    val securityCheck: SecurityCheck,
    val transactionResult: TransactionResult,
    val additionalDataFields: List<AdditionalDataField>,
    val emvTags: List<EmvTag>
)