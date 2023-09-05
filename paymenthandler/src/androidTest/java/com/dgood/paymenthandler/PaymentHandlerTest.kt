package com.dgood.paymenthandler

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.dgood.paymenthandler.model.Card
import com.dgood.paymenthandler.model.Tag
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PaymentHandlerTest {

    private lateinit var paymentHandler: PaymentHandler
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        paymentHandler = PaymentHandler(context)
    }

    @Test
    fun testInsertCardTransaction() {
        val amount = 50.0
        val order = "12345A"
        val currency = "USD"
        val expectedResult = "Please Insert Card: \nAmount - 12345A 50.0 \nOrder - USD"

        val result = paymentHandler.insertCardTransaction(amount, order, currency)
        assertEquals(expectedResult, result)
    }

    @Test
    fun testGetTlvString() {

        val card = Card(null, null, null, null, null,
            listOf(
                Tag("TAG1", "010203"),
                Tag("TAG2", "04050607"),
                Tag("TAG3", "08")
            )
        )

        val expectedTlvString = "TAG103010203TAG20404050607TAG30108"

        val resultTlvString = paymentHandler.getTlvString(card)

        assertEquals(expectedTlvString, resultTlvString)
    }

    @Test
    fun testGetRandomCardData() {
        setup()
        val sampleXmlData = paymentHandler.readRawResourceAsString(context, R.raw.sample_card)
        val cardList = paymentHandler.parseCards(sampleXmlData)
        val randomCard = paymentHandler.getRandomCardData()

        Assert.assertTrue(cardList.contains(randomCard))
    }

    @Test
    fun testFormatTimestamp() {
        val timestamp = "2023-09-03T04:03:14.558-05:00"
        val expectedFormattedTimestamp = "10:03 03/09/2023"

        val formattedTimestamp = paymentHandler.formatTimestamp(timestamp)

        assertEquals(expectedFormattedTimestamp, formattedTimestamp)
    }

}