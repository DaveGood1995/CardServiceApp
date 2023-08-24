package com.dgood.paymenthandler

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.dgood.paymenthandler.model.request.CustomerAccount
import com.dgood.paymenthandler.model.request.Device
import com.dgood.paymenthandler.model.request.Order
import com.dgood.paymenthandler.model.response.OrderBreakdown
import com.dgood.paymenthandler.model.response.TransactionResponse
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

    @Test
    fun testMakePayment(){
        val channel = "POS"
        val terminal = "5140001"
        val order = Order("QM1UKY", "USD", 5.0)
        val device = Device("PAX_A920_PRO", "F8765432110000000079")
        val account = CustomerAccount(device, "DF79033535335F280208409F6E2008400000303000000000000000000000000000000000000000000000000000009F120A4D6173746572636172649F110101500A4D4153544552434152445F24032512315F25032103018C279F02069F03069F1A0295055F2A029A039C019F37049F35019F45029F4C089F34039F21039F7C148D0C910A8A0295059F37049F4C088E0C00000000000000005E031F039F0702FF009F0D05B4508080009F0E0500000000009F0F05B4708080009F160A393432353832313733339F1C0831323334353637389F1E0831383530303130389F420208409F4E0E57616C6D6172744E6577596F726BDF780518500108689F4104000005019F10120111A04009220400000000000000000000FF9F090200028407A00000000410105F3401015F2D02656E9F370410A4E2809C01009F21031529379B0200009A032209055F2A020840950500000080019F3501229F1A0208409F3303E008089F3901079F2701809F34031F03029F3602013F820219809F0607A00000000410104F07A00000000410109F260881BF9F3FEF7DD0B79F03060000000000009F02060000000005009F40056000F0B0019F530146C22043D764D2C035C0CEF759BAB058E868BCAC1A11F71278E611DFDF2D99F87538B5C00AF87654321100000000795F200D4E6F7420417661696C61626C65DF78051850010868", "EMV")
       val orderBreakdown = OrderBreakdown(5.00)
        val orderResult = Order("QM1UKY", "USD", 5.00, orderBreakdown, )
        val expectedResult = TransactionResponse("EQZB2G8K1V", "136007", "", "")


        "order": {
            "orderId": "QM1UKY",
            "currency": "USD",
            "totalAmount": 5.00,
            "orderBreakdown": {
            "subtotalAmount": 5.00
        }
        },
        "customerAccount": {
            "cardType": "MasterCard",
            "cardholderName": "Not Available",
            "maskedPan": "541333******4111",
            "expiryDate": "1225",
            "entryMethod": "CONTACTLESS ICC"
        },
        "securityCheck": {
            "cvvResult": "M",
            "avsResult": "Y"
        },
        "transactionResult": {
            "type": "SALE",
            "status": "READY",
            "approvalCode": "OK586",
            "dateTime": "2022-09-05T11:08:59.417-04:00",
            "currency": "USD",
            "authorizedAmount": 5.00,
            "resultCode": "A",
            "resultMessage": "OK586",
            "storedPaymentCredentials": {
            "terminal": "136007",
            "merchantReference": "MREF_5f88ae93-c923-4e6c-adbd-acfff9801fb9B6",
            "cardholderName": "Test Card 07 Uat Usa",
            "credentialsNumber": "2967536247202906",
            "maskedPan": "541333******4111",
            "securityCheck": "CVV_VALIDATED"
        }
        },
        "additionalDataFields": [
        {
            "name": "ORDER_NUM",
            "value": "09139515"
        }
        ],
        "emvTags": [
        {
            "hex": "91",
            "value": "26a6e3d08861c4e23030"
        },
        {
            "hex": "84",
            "value": "a0000000041010"
        },
        {
            "hex": "8A",
            "value": "3030"
        }
        ]
    }

        val result = paymentHandler.makePayment(channel, terminal, order, device, account)
    }
}