package com.dgood.paymenthandler

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.dgood.paymenthandler.model.request.RequestCustomerAccount
import com.dgood.paymenthandler.model.request.Device
import com.dgood.paymenthandler.model.request.RequestOrder
import com.dgood.paymenthandler.model.response.Order
import com.dgood.paymenthandler.model.response.OrderBreakdown
import com.dgood.paymenthandler.model.response.CustomerAccount
import com.dgood.paymenthandler.model.response.StoredPaymentCredentials
import com.dgood.paymenthandler.model.response.TransactionResult
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
        val requestOrder = RequestOrder("SDHLGB", "USD", 5.0)
        val device = Device("PAX_A920_PRO", "f8765432110000000082")
//        val account = RequestCustomerAccount(device, "EMV","DF79033535335F280208409F6E2008400000303000000000000000000000000000000000000000000000000000009F120A4D6173746572636172649F110101500A4D4153544552434152445F24032512315F25032103018C279F02069F03069F1A0295055F2A029A039C019F37049F35019F45029F4C089F34039F21039F7C148D0C910A8A0295059F37049F4C088E0C00000000000000005E031F039F0702FF009F0D05B4508080009F0E0500000000009F0F05B4708080009F160A393432353832313733339F1C0831323334353637389F1E0831383530303130389F420208409F4E0E57616C6D6172744E6577596F726BDF780518500108689F4104000005019F10120111A04009220400000000000000000000FF9F090200028407A00000000410105F3401015F2D02656E9F370410A4E2809C01009F21031529379B0200009A032209055F2A020840950500000080019F3501229F1A0208409F3303E008089F3901079F2701809F34031F03029F3602013F820219809F0607A00000000410104F07A00000000410109F260881BF9F3FEF7DD0B79F03060000000000009F02060000000005009F40056000F0B0019F530146C22043D764D2C035C0CEF759BAB058E868BCAC1A11F71278E611DFDF2D99F87538B5C00AF87654321100000000795F200D4E6F7420417661696C61626C65DF78051850010868")
        val account = RequestCustomerAccount(device, "EMV","DF7902353533035F28020840029F6E020840000030300000000000000000000000000000000000000000000000000000209F12024D6173746572636172640A9F1102010150014D4153544552434152440A5F2402251231035F2502210301038C019F02069F03069F1A0295055F2A029A039C019F37049F35019F45029F4C089F34039F21039F7C14278D01910A8A0295059F37049F4C080C8E0100000000000000005E031F030C9F0702FF00029F0D02B450808000059F0E020000000000059F0F02B470808000059F1602393432353832313733330A9F1C023132333435363738089F1E023138353030313038089F42020840029F4E0257616C6D6172744E6577596F726B0EDF78021850010868059F410200000501049F10020111A04009220400000000000000000000FF129F09020002028401A0000000041010075F340201015F2D02656E029F370210A4E280049C0100019F2102152937039B010000029A01220905035F2A0208400295010000008001059F350222019F1A020840029F3302E00808039F390207019F270280019F34021F0302039F3602013F0282011980029F0602A0000000041010074F01A0000000041010079F260281BF9F3FEF7DD0B7089F0302000000000000069F0202000000000500069F40026000F0B001059F53024601C2015084A1C8D19C81561F69C430A8A22FA6E85065433502ADEF2B7B760DDACEC95820C001F87654321100000000820A5F200254657374204361726420340BDF7802185001086805")
        val orderBreakdown = OrderBreakdown(5.00)
        val orderResult = Order("QM1UKB", "USD", 5.00, orderBreakdown, )
        val accountResult = CustomerAccount("MasterCard", "Not Available", "541333******4111","1225","CONTACTLESS ICC")
//        val expectedResult = TransactionResult("EQZB2G8K1V", "136007", orderResult, "")
        val cred = StoredPaymentCredentials("", "", "", "", "", "")
        val expectedResult = TransactionResult("", "", "", "", "", 5.0, "", "", cred )


        val result = paymentHandler.makePayment(channel, terminal, requestOrder, account)
        assertEquals(expectedResult, result)

    }
}