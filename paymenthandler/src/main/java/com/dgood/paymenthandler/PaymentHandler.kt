package com.dgood.paymenthandler

import android.content.Context
import com.dgood.paymenthandler.model.Card
import com.dgood.paymenthandler.model.Tag
import com.dgood.paymenthandler.model.request.RequestCustomerAccount
import com.dgood.paymenthandler.model.request.RequestOrder
import com.dgood.paymenthandler.model.request.TransactionRequest
import com.dgood.paymenthandler.model.response.TransactionResponse
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Random
import javax.xml.parsers.DocumentBuilderFactory

class PaymentHandler(private val context: Context?) {

    private val serviceClient = ServiceClient(context)

    fun insertCardTransaction(amount: Double, order: String, currency: String): String {
        return context!!.resources.getString(R.string.start_transaction_template)
            .format(currency, amount, order)
    }

    fun makePayment(
        channel: String,
        terminal: String,
        requestOrder: RequestOrder,
        account: RequestCustomerAccount
    ): TransactionResponse {

        val transactionRequest = TransactionRequest(channel, terminal, requestOrder, account)

        return serviceClient.makePayment(transactionRequest)
    }

    private fun readRawResourceAsString(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))

        val xmlData = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            xmlData.append(line)
        }
        reader.close()

        return xmlData.toString()
    }

    private fun hexStringToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private fun parseCards(xml: String): List<Card> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(xml.byteInputStream())

        val cardList = mutableListOf<Card>()

        val cardElements: NodeList = document.getElementsByTagName("card")
        for (i in 0 until cardElements.length) {
            val cardElement = cardElements.item(i) as Element

            val cardholdername = cardElement.getElementsByTagName("cardholdername").item(0)?.textContent
            val dataKsn = cardElement.getElementsByTagName("dataKsn").item(0)?.textContent
            val serialNumber = cardElement.getElementsByTagName("serialNumber").item(0)?.textContent
            val encryptedData = cardElement.getElementsByTagName("encryptedData").item(0)?.textContent
            val payloadType = cardElement.getElementsByTagName("payloadType").item(0)?.textContent

            val tags = mutableListOf<Tag>()
            val tagElements = cardElement.getElementsByTagName("tags")
            for (j in 0 until tagElements.length) {
                val tagElement = tagElements.item(j) as Element
                val key = tagElement.getAttribute("key")
                val value = tagElement.getAttribute("value")
                tags.add(Tag(key, value))
            }

            cardList.add(Card(cardholdername, dataKsn, serialNumber, encryptedData, payloadType, tags))
        }

        return cardList
    }

    fun getRandomCardData(): Card {
        val resourceId = R.raw.sample_card
        val xmlData = readRawResourceAsString(context!!, resourceId)

        val cardList = parseCards(xmlData)

        val random = Random()

        return cardList[random.nextInt(cardList.size)]
    }

    fun getTlvString(selectedCard: Card): String {
        val tlvStringBuilder = StringBuilder()

        for (tag in selectedCard.tags) {
            val key = tag.key
            val value = tag.value

            val valueBytes = hexStringToByteArray(value)

            tlvStringBuilder.append(key)
            tlvStringBuilder.append(String.format("%02X", valueBytes.size))
            tlvStringBuilder.append(value)
        }

        return tlvStringBuilder.toString()
    }

}