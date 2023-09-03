package com.dgood.cardservicesapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dgood.cardservicesapp.databinding.FragmentReceiptListBinding
import com.dgood.paymenthandler.ReceiptDatabaseHelper
import com.dgood.paymenthandler.model.response.Receipt

class ReceiptListFragment : Fragment() {

    private var _binding: FragmentReceiptListBinding? = null
    private val binding get() = _binding!!

    private lateinit var receiptDatabaseHelper: ReceiptDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReceiptListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiptDatabaseHelper = ReceiptDatabaseHelper(requireContext())

        recyclerView = binding.recyclerViewReceipts
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val receiptPairs: List<Pair<Long, Receipt>> = receiptDatabaseHelper.getAllReceipts()

        adapter = ReceiptListAdapter(receiptPairs) { databaseId ->
            val bundle = Bundle()
            bundle.putLong("id", databaseId)
            findNavController().navigate(
                R.id.action_ReceiptListFragment_to_ReceiptDetailFragment,
                bundle
            )
        }

        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}