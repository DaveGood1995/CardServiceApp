package com.dgood.paymenthandler.model.response

data class OrderBreakdown(
    val subtotalAmount: Double
)

data class ResponseOrder(
    val orderId: String,
    val currency: String,
    val totalAmount: Double,
    val orderBreakdown: OrderBreakdown
)

data class ResponseCustomerAccount(
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
    val responseOrder: ResponseOrder,
    val responseCustomerAccount: ResponseCustomerAccount,
    val securityCheck: SecurityCheck,
    val transactionResult: TransactionResult,
    val additionalDataFields: List<AdditionalDataField>,
    val emvTags: List<EmvTag>
)