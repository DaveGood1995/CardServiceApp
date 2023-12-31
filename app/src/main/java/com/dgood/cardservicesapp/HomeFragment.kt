package com.dgood.cardservicesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dgood.cardservicesapp.databinding.FragmentHomeBinding
import com.dgood.paymenthandler.ReceiptDatabaseHelper

private lateinit var receiptDatabaseHelper: ReceiptDatabaseHelper

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for buttons to navigate to other fragments
        binding.buttonSubmitPayment.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_PaymentFragment)
        }
        binding.buttonViewReceipts.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_ReceiptListFragment)
        }
        binding.buttonAppInfo.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_AppHelpFragment)
        }

        // Initialize the ReceiptDatabaseHelper with the application context
        receiptDatabaseHelper = ReceiptDatabaseHelper(requireContext())

        // Check if there are receipts in the database and hide the corresponding button if none exist
        if (!receiptDatabaseHelper.hasReceipts()) {
            binding.buttonViewReceipts.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





