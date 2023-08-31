package com.dgood.paymenthandler.model

data class Card(
    val cardholdername: String?,
    val dataKsn: String?,
    val serialNumber: String?,
    val encryptedData: String?,
    val payloadType: String?,
    val tags: List<Tag>
)

data class Tag(
    val key: String,
    val value: String
)

data class Cards(
    val cardList: List<Card>
)