package com.dgood.paymenthandler.model.response

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

data class Receipt(
    val formattedReceipt: String,
    val transactionResponse: TransactionResponse
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

fun formatReceipt(transactionResponse: TransactionResponse): String {
    val formattedReceipt = StringBuilder()

    formattedReceipt.appendLine("Receipt for Transaction: ${transactionResponse.uniqueReference}")
    formattedReceipt.appendLine("--------------------------------")

    formattedReceipt.appendLine("Order ID: ${transactionResponse.order.orderId}")
    formattedReceipt.appendLine("Currency: ${transactionResponse.order.currency}")
    formattedReceipt.appendLine("Total Amount: $${transactionResponse.order.totalAmount}")

    formattedReceipt.appendLine("\nCustomer Account:")
    formattedReceipt.appendLine("  Card Type: ${transactionResponse.customerAccount.cardType}")
    formattedReceipt.appendLine("  Cardholder Name: ${transactionResponse.customerAccount.cardholderName}")
    formattedReceipt.appendLine("  Masked PAN: **** **** **** ${transactionResponse.customerAccount.maskedPan.takeLast(4)}")
    formattedReceipt.appendLine("  Expiry Date: ${transactionResponse.customerAccount.expiryDate}")
    formattedReceipt.appendLine("  Entry Method: ${transactionResponse.customerAccount.entryMethod}")

    formattedReceipt.appendLine("\nSecurity Check:")
    formattedReceipt.appendLine("  CVV Result: ${transactionResponse.securityCheck.cvvResult}")
    formattedReceipt.appendLine("  AVS Result: ${transactionResponse.securityCheck.avsResult}")

    formattedReceipt.appendLine("\nTransaction Result:")
    formattedReceipt.appendLine("  Type: ${transactionResponse.transactionResult.type}")
    formattedReceipt.appendLine("  Status: ${transactionResponse.transactionResult.status}")
    formattedReceipt.appendLine("  Approval Code: ${transactionResponse.transactionResult.approvalCode}")
    formattedReceipt.appendLine("  Date & Time: ${transactionResponse.transactionResult.dateTime}")
    formattedReceipt.appendLine("  Currency: ${transactionResponse.transactionResult.currency}")
    formattedReceipt.appendLine("  Authorized Amount: $${transactionResponse.transactionResult.authorizedAmount}")
    formattedReceipt.appendLine("  Result Code: ${transactionResponse.transactionResult.resultCode}")
    formattedReceipt.appendLine("  Result Message: ${transactionResponse.transactionResult.resultMessage}")

    // ... Continue formatting other sections ...

    formattedReceipt.appendLine("--------------------------------")
    formattedReceipt.appendLine("Thank you for your purchase!")

    return formattedReceipt.toString()
}