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
import com.dgood.paymenthandler.model.ErrorResponse
import com.dgood.paymenthandler.model.request.CardDetails
import com.dgood.paymenthandler.model.request.Device
import com.dgood.paymenthandler.model.request.RequestCustomerAccount
import com.dgood.paymenthandler.model.request.RequestOrder
import com.dgood.paymenthandler.model.response.Receipt
import com.dgood.paymenthandler.model.response.formatReceipt
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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

            }
        }
    }

    private suspend fun handleCardInsertion() {

        delay(5000)

        val randomCard = paymentHandler.getRandomCardData()
        insertCardDialog?.dismiss()
        showCardInsertedDialog()

        delay(5000)

        cardInserteDialog?.dismiss()
        showGoingOnlineDialog()
        makePayment(randomCard)
    }

    private fun startTransaction() {
        val amountString = binding.amountEditText.text.toString()
        val orderId = binding.orderIdEditText.text.toString()

        val orderIdPattern = "^[a-zA-Z0-9]{1,6}$"
        if (!orderId.matches(orderIdPattern.toRegex())) {
            showError("Invalid order ID. It must be alphanumeric and up to 6 characters long.")
            return
        }

        try {
            val amount = amountString.toDouble()
            if (amount <= 0.0) {
                showError(resources.getString(R.string.amount_validation_message))
                return
            }
        } catch (e: NumberFormatException) {
            showError(resources.getString(R.string.order_id_validation_message))
            return
        }

        val currency = binding.currencySpinner.selectedItem.toString()
        val message = paymentHandler.insertCardTransaction(amountString.toDouble(), currency, orderId)

        showInsertCardDialog(message)

        coroutineScope?.launch {
            handleCardInsertion()
        }
    }
    private fun showError(errorMessage: String) {
        Snackbar.make(
            binding.root,
            errorMessage,
            Snackbar.LENGTH_LONG
        ).show()
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
            .setTitle(resources.getString(R.string.please_insert_card))
            .setMessage(message)
            .setPositiveButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

       insertCardDialog?.show()
    }

    private fun showCardInsertedDialog() {
        cardInserteDialog = AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.card_inserted))
            .setMessage(resources.getString(R.string.card_now_inserted))
            .setPositiveButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

       cardInserteDialog?.show()
    }

    private fun showGoingOnlineDialog() {
        goingOnlineDialog = AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.going_online))
            .setMessage(resources.getString(R.string.transaction_in_progress))
            .setPositiveButton(resources.getString(R.string.cancel)) { dialog, _ ->
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
            if (paymentCard.payloadType.equals("EMV")) {
                val device = Device(
                    resources.getString(R.string.deviceType),
                    paymentCard.dataKsn.toString(),
                    null
                )
                RequestCustomerAccount(
                    device,
                    paymentCard.payloadType.toString(),
                    paymentHandler.getTlvString(paymentCard),
                    null,
                    null
                )
            } else {
                val device = Device(
                    resources.getString(R.string.deviceType),
                    paymentCard.dataKsn.toString(),
                    paymentCard.serialNumber
                )
                val cardDetails = CardDetails(device, paymentCard.encryptedData.toString())
                RequestCustomerAccount(
                    null,
                    paymentCard.payloadType.toString(),
                    null,
                    cardDetails,
                    paymentCard.cardholdername
                )
            }

        lifecycleScope.launch {
            try {
                val localTransactionResponse = withContext(Dispatchers.IO) {
                    paymentHandler.makePayment(channelId, terminal, order, customerAccount)
                }

                val receipt = Receipt(
                    formatReceipt(localTransactionResponse),
                    localTransactionResponse,
                    localTransactionResponse.order.orderId,
                    paymentHandler.formatTimestamp(
                        localTransactionResponse.transactionResult.dateTime
                    ),
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
            } catch (e: Exception) {
                goingOnlineDialog!!.dismiss()
                val errorMessage = e.message

                val alertDialogBuilder = AlertDialog.Builder(context)

                val titleStartIndex = errorMessage!!.indexOf(":")
                val title = if (titleStartIndex >= 0) {
                    errorMessage.substring(0, titleStartIndex).trim()
                } else {
                    "Error"
                }

                alertDialogBuilder.setTitle(title)

                try {
                    val jsonStartIndex = errorMessage.indexOf("{")
                    val jsonEndIndex = errorMessage.lastIndexOf("}")

                    if (jsonStartIndex >= 0 && jsonEndIndex >= 0 && jsonStartIndex < jsonEndIndex) {
                        val jsonContent = errorMessage.substring(jsonStartIndex, jsonEndIndex + 1)

                        val errorResponse = Gson().fromJson(jsonContent, ErrorResponse::class.java)

                        val errorCode = errorResponse.details.firstOrNull()?.errorCode ?: "Unknown"
                        val errorMessage = errorResponse.details.firstOrNull()?.errorMessage ?: "No description"

                        val readableErrorMessage = "Code: $errorCode\nDescription: $errorMessage"

                        alertDialogBuilder.setMessage(readableErrorMessage)
                    } else {
                        alertDialogBuilder.setMessage(errorMessage)
                    }
                } catch (jsonException: JsonSyntaxException) {
                    alertDialogBuilder.setMessage(errorMessage)
                }
                alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
    }

    private fun generateRandomId() {
        val randomAlphanumericString = generateRandomOrderId()
        binding.orderIdEditText.setText(randomAlphanumericString)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
