package com.dgood.cardservicesapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dgood.cardservicesapp.databinding.FragmentPaymentBinding
import com.dgood.paymenthandler.PaymentHandler
import com.dgood.paymenthandler.ReceiptDatabaseHelper
import com.dgood.paymenthandler.model.Card
import com.dgood.paymenthandler.model.request.CardDetails
import com.dgood.paymenthandler.model.request.Device
import com.dgood.paymenthandler.model.request.RequestCustomerAccount
import com.dgood.paymenthandler.model.request.RequestOrder
import com.dgood.paymenthandler.model.response.Receipt
import com.dgood.paymenthandler.model.response.formatReceipt
import kotlin.random.Random
import kotlinx.coroutines.*


class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var paymentHandler: PaymentHandler
    private lateinit var receiptDatabaseHelper: ReceiptDatabaseHelper
    private var insertCardDialog: AlertDialog? = null
    private var cardInserteDialog: AlertDialog? = null
    private var goingOnlineDialog: AlertDialog? = null
    private var coroutineScope: CoroutineScope? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentHandler = PaymentHandler(requireContext())
        receiptDatabaseHelper = ReceiptDatabaseHelper(requireContext())
        coroutineScope = CoroutineScope(Dispatchers.Main)


        generateRandomId()

        val currencyOptions = resources.getStringArray(R.array.currency_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.currencySpinner.adapter = adapter

        binding.generateOrderIdButton.setOnClickListener {
            generateRandomId()
        }

        binding.makePaymentButton.setOnClickListener {
            startTransaction()
        }

        binding.currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.currencySymbol.text = resources.getStringArray(R.array.currency_symbols)[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Handle if needed
            }
        }
    }

    private suspend fun handleCardInsertion() {
        // Add a delay for 5 seconds
        delay(5000)

        // After the delay, perform the actions
        val randomCard = paymentHandler.getRandomCardData()
        insertCardDialog?.dismiss()
        showCardInsertedDialog()

        // Add another delay for 5 seconds
        delay(5000)

        // After the second delay, perform the actions
        cardInserteDialog?.dismiss()
        showGoingOnlineDialog()
        makePayment(randomCard)
    }


    private fun startTransaction(){
        val message = paymentHandler.insertCardTransaction(
            binding.amountEditText.text.toString().toDouble(),
            binding.currencySpinner.selectedItem.toString(),
            binding.orderIdEditText.text.toString())

        showInsertCardDialog(message)

        coroutineScope?.launch {
            handleCardInsertion()
        }


    }

    private fun generateRandomOrderId(): String {
        val alphanumericChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random.Default

        return (1..6)
            .map { alphanumericChars[random.nextInt(0, alphanumericChars.length)] }
            .joinToString("")
    }

    private fun showInsertCardDialog(message: String) {
        insertCardDialog = AlertDialog.Builder(context)
            .setTitle("Please Insert Card")
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

       insertCardDialog?.show()
    }

    private fun showCardInsertedDialog() {
        cardInserteDialog = AlertDialog.Builder(context)
            .setTitle("Card Inserted")
            .setMessage("Card is now inserted")
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

       cardInserteDialog?.show()
    }

    private fun showGoingOnlineDialog() {
        goingOnlineDialog = AlertDialog.Builder(context)
            .setTitle("Going Online")
            .setMessage("Transaction in progress...")
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        goingOnlineDialog?.show()
    }

    private fun makePayment(paymentCard: Card) {
        val channelId = resources.getString(R.string.channel)
        val terminal = resources.getString(R.string.terminal)
        val orderId = binding.orderIdEditText.text.toString()
        val currency = binding.currencySpinner.selectedItem.toString()
        val amount = binding.amountEditText.text.toString().toDouble()
        val order = RequestOrder(orderId, currency, amount)

        val customerAccount: RequestCustomerAccount =
            if(paymentCard.payloadType.equals("EMV")){
                val device = Device(resources.getString(R.string.deviceType), paymentCard.dataKsn.toString(),null)
                RequestCustomerAccount(
                    device,
                    paymentCard.payloadType.toString(),
                    paymentHandler.getTlvString(paymentCard),
                    null,
                    null
             )
            } else {
                val device = Device(resources.getString(R.string.deviceType), paymentCard.dataKsn.toString(),paymentCard.serialNumber)
                val cardDetails = CardDetails(device , paymentCard.encryptedData.toString())
                RequestCustomerAccount(
                    null,
                    paymentCard.payloadType.toString(),
                    null,
                    cardDetails,
                    paymentCard.cardholdername
                )
            }

        lifecycleScope.launch {
            val localTransactionResponse = withContext(Dispatchers.IO) {
                paymentHandler.makePayment(channelId, terminal, order, customerAccount)
            }

//            binding.progressBar.visibility = View.GONE

            localTransactionResponse.transactionResult


            val receipt = Receipt(
                formatReceipt(localTransactionResponse),
                localTransactionResponse,
                localTransactionResponse.order.orderId,
                localTransactionResponse.transactionResult.dateTime,
                localTransactionResponse.order.totalAmount,
                localTransactionResponse.order.currency
            )
            val insertedId = receiptDatabaseHelper.insertReceipt(receipt)

            goingOnlineDialog?.dismiss()

            val bundle = Bundle()
            bundle.putLong("id", insertedId)
            findNavController().navigate(
                R.id.action_PaymentFragment_to_ReceiptDetailFragment,
                bundle
            )
        }
    }

    private fun generateRandomId() {
        val randomAlphanumericString = generateRandomOrderId()
        binding.orderIdEditText.text = randomAlphanumericString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
