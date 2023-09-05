package com.dgood.cardservicesapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dgood.cardservicesapp.databinding.FragmentReceiptDetailBinding
import com.dgood.paymenthandler.PaymentHandler
import com.dgood.paymenthandler.ReceiptDatabaseHelper

class ReceiptDetailFragment : Fragment() {

    private var _binding: FragmentReceiptDetailBinding? = null

    private val binding get() = _binding!!

    private lateinit var paymentHandler: PaymentHandler
    private lateinit var receiptDatabaseHelper: ReceiptDatabaseHelper
    private lateinit var receiptDatabaseId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentHandler = PaymentHandler(requireContext())
        receiptDatabaseHelper = ReceiptDatabaseHelper(requireContext())

        // Set click listener for the homeButton
        binding.homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_ReceiptDetailFragment_to_HomeFragment)
        }

        // Set click listener for the deleteReceiptButton
        binding.deleteReceiptButton.setOnClickListener {
            showConfirmationDialog()
        }

        // Get the receipt ID from the arguments and convert it to a String
        val id = arguments?.getLong("id")
        receiptDatabaseId = id.toString()

        // Get the formatted receipt from the database and display it
        val formattedReceipt = receiptDatabaseHelper.getReceiptById(id)?.formattedReceipt
        _binding?.textviewReceipt!!.text = formattedReceipt
    }

    // Function to show a confirmation dialog for deleting a receipt
    private fun showConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.confirm_delete_dialog_title))
            .setMessage(resources.getString(R.string.confirm_delete_dialog_message))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                // Delete the receipt from the database
                receiptDatabaseHelper.deleteReceipt(receiptDatabaseId.toLong())
                dialog.dismiss()
                if (receiptDatabaseHelper.hasReceipts()) {
                    // Navigate to ReceiptListFragment if there are more receipts
                    findNavController().navigate(R.id.action_ReceiptDetailFragment_to_ReceiptListFragment)
                } else {
                    // Navigate to HomeFragment if there are no more receipts
                    findNavController().navigate(R.id.action_ReceiptDetailFragment_to_HomeFragment)
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()

        alertDialog.show()
    }
}