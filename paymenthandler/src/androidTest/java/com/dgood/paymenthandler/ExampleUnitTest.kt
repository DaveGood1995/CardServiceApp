package com.dgood.paymenthandler

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PaymentHandlerTest {

    private lateinit var paymentHandler: PaymentHandler

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        paymentHandler = PaymentHandler(context)
    }

    @Test
    fun testInsertCardTransaction() {
        val amount = 50.0
        val order = "QM1UKY"
        val currency = "EUR"
        val expectedMessage = "Card Inserted: Amount - EUR 50.00, Order - QM1UKY"

        val result = paymentHandler.insertCardTransaction(amount, order, currency)

        assertEquals(expectedMessage, result)
    }
}