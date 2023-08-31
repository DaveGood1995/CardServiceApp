package com.dgood.cardservicesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.dgood.cardservicesapp.databinding.FragmentSecondBinding
import com.dgood.paymenthandler.PaymentHandler
import com.dgood.paymenthandler.model.request.Device
import com.dgood.paymenthandler.model.request.RequestCustomerAccount
import com.dgood.paymenthandler.model.request.RequestOrder
import com.dgood.paymenthandler.model.response.TransactionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var paymentHandler: PaymentHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentHandler = PaymentHandler(requireContext())

        val currencyOptions = resources.getStringArray(R.array.currency_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.currencySpinner.adapter = adapter

        binding.generateOrderIdButton.setOnClickListener {
            generateRandomId()
        }

        binding.makePaymentButton.setOnClickListener {
            makePayment()
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

    private fun generateRandomOrderId(length: Int): String {
        val alphanumericChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random.Default

        return (1..length)
            .map { alphanumericChars[random.nextInt(0, alphanumericChars.length)] }
            .joinToString("")
    }

    private fun makePayment() {
        val channelId = resources.getString(R.string.channel)
        val terminal = resources.getString(R.string.terminal)
        val orderId = binding.orderIdEditText.text.toString()
        val currency = binding.currencySpinner.selectedItem.toString()
        val amount = binding.amountEditText.text.toString().toDouble()
        val order = RequestOrder(orderId, currency, amount)

        val paymentCard = paymentHandler.getRandomCardData()
        val device = Device(resources.getString(R.string.deviceType), paymentCard.dataKsn.toString())
        val customerAccount = RequestCustomerAccount(device, paymentCard.payloadType.toString(), paymentHandler.getTlvString(paymentCard))
        var transactionResponse: TransactionResponse? = null

        lifecycleScope.launch {
            val localTransactionResponse = withContext(Dispatchers.IO) {
                paymentHandler.makePayment(channelId, terminal, order, customerAccount)
            }

//            binding.progressBar.visibility = View.GONE

            localTransactionResponse.transactionResult
        }
    }






    private fun generateRandomId() {
        val randomAlphanumericString = generateRandomOrderId(6)
        binding.orderIdEditText.setText(randomAlphanumericString)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
