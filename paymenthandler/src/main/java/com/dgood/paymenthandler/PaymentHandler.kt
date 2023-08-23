package com.dgood.paymenthandler

import android.content.Context
import com.dgood.paymenthandler.Model.CardPayload
import com.dgood.paymenthandler.Model.Channel
import com.dgood.paymenthandler.Model.Operator
import com.dgood.paymenthandler.Model.Terminal
import com.dgood.paymenthandler.Model.TransactionRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class PaymentHandler(private val context: Context) {

    fun insertCardTransaction(amount: Double, order: String, currency: String): String {
        val message = "Card Inserted: Amount - %s %.2f, Order - %s".format(currency, amount, order)
        return message
    }

    fun makePayment(){
        val paymentRequest = TransactionRequest(
            channel = Channel(),
            terminal = Terminal(terminal = "YourTerminalNumber"),
            operator = Operator(operator = "YourOperatorName"),
            // Define other fields
        )

        val paymentResponse = serviceClient.makePayment(paymentRequest)
        if (paymentResponse != null) {
            println("Payment successful!")
        } else {
            println("Payment failed")
        }
    }

    fun getCardPayload(): String {
        var payload = ""

        try {
            // Load XML data from the values directory
            val xmlData = """
            <cards>
                <!-- Paste the provided XML card data here -->
            </cards>
        """.trimIndent()

            // Create a DocumentBuilderFactory and parse the XML data
            val docBuilderFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docBuilderFactory.newDocumentBuilder()
            val inputSource = InputSource(StringReader(xmlData))
            val document = docBuilder.parse(inputSource)
            document.documentElement.normalize()

            // Get a list of <card> elements
            val cardElements: NodeList = document.getElementsByTagName("card")

            // Iterate through each <card> element
            for (i in 0 until cardElements.length) {
                val cardNode = cardElements.item(i)
                if (cardNode.nodeType == Element.ELEMENT_NODE) {
                    val cardElement = cardNode as Element
                    val dataKsn = cardElement.getElementsByTagName("dataKsn").item(0).textContent
                    val payloadType = cardElement.getElementsByTagName("payloadType").item(0).textContent
                    // ... (similarly, extract other tag values)

                    // Prepare data to send to the REST server
                    payload = """
                    {
                        "dataKsn": "$dataKsn",
                        "payloadType": "$payloadType"
                        // ... (add other relevant fields)
                    }
                """.trimIndent()

                }
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
        return payload
    }
}