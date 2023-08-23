package com.dgood.paymenthandler.Model

data class Channel(
    val channel: String = "POS"
)

data class Terminal(
    val terminal: String
)

data class Operator(
    val operator: String
)

data class Customer(
    val customer: String
)

data class CardPayload(
    val cardData: String
)

data class CredentialOnFile(
    val credentials: String
)

data class IpAddress(
    val ipAddress: String
)

data class OfflineProcessing(
    val offlineProcessing: String
)

data class CustomField(
    val fieldName: String,
    val fieldValue: String
)

data class TransactionRequest(
    val channel: Channel,
    val terminal: Terminal,
    val operator: Operator,
    val order: Order,
    val autoCapture: Boolean = true,
    val processAsSale: Boolean = false,
    val offlineProcessing: OfflineProcessing? = null,
    val additionalDataFields: List<CustomField>? = null
)

data class Order(
    val customer: Customer,
    val customerAccount: CardPayload,
    val credentialOnFile: CredentialOnFile? = null,
    val ipAddress: IpAddress,
)