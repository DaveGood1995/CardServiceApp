package com.dgood.cardservicesapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dgood.cardservicesapp.databinding.FragmentAppHelpBinding

class AppHelpFragment : Fragment() {

    private var _binding: FragmentAppHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAppHelpBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the app's version name and display it in a TextView
        val packageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
        val versionName = packageInfo.versionName

        val versionTextView = binding.appVersion
        versionTextView.text = String.format(resources.getString(R.string.version_template), versionName)

        // Set a click listener for the "Contact Developer" button to send an email
        binding.buttonContactDeveloper.setOnClickListener {
            sendEmail()
        }

    }

    // Override onDestroyView to release the binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function to send an email to the developer
    private fun sendEmail() {
        val emailAddress = "davidwgood@gmail.com"
        val subject = "Payment Processor Support"
        val message = "Phone Make:\nPhone Model:\nAndroid Version:\nHow can I help?\n"

        // Create an email intent with recipient, subject, and message
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        // Start the email intent to allow the user to send an email
        startActivity(emailIntent)
    }
}
