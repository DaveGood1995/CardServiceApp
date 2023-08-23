package com.dgood.cardservicesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.dgood.cardservicesapp.databinding.FragmentSecondBinding
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var orderIdEditText: EditText
    private lateinit var currencySpinner: Spinner
    private lateinit var generateOrderIdButton: Button
    private lateinit var currencyOptions: Array<String>
    private lateinit var currencySymbolList: Array<String>
    private lateinit var currencySymbol: TextView
    private val binding get() = _binding!!
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_second, container, false)

        orderIdEditText = rootView.findViewById(R.id.order_id_edit_text)
        currencySpinner = rootView.findViewById(R.id.currency_spinner)
        generateOrderIdButton = rootView.findViewById(R.id.generate_order_id_button)
        currencyOptions = resources.getStringArray(R.array.currency_options)
        currencySymbolList = resources.getStringArray(R.array.currency_symbols)
        currencySymbol = rootView.findViewById(R.id.currency_symbol)

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = adapter

        generateOrderIdButton.setOnClickListener {
            generateRandomId()
        }

        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currencySymbol.text = currencySymbolList.get(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        }

    fun generateRandomOrderId(length: Int): String {
        val alphanumericChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random.Default

        return (1..length)
            .map { alphanumericChars[random.nextInt(0, alphanumericChars.length)] }
            .joinToString("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun generateRandomId() {
        val randomAlphanumericString = generateRandomOrderId(6)
        orderIdEditText.setText(randomAlphanumericString)
    }

}