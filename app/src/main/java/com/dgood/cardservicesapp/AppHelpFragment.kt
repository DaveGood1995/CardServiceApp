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

        val packageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
        val versionName = packageInfo.versionName

        val versionTextView = binding.appVersion
        versionTextView.text = String.format(resources.getString(R.string.version_template), versionName)

        binding.buttonContactDeveloper.setOnClickListener {
            sendEmail()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun sendEmail() {
        val emailAddress = "davidwgood@gmail.com"
        val subject = "Payment Processor Support"
        val message = "Phone Make:\nPhone Model:\nAndroid Version:\nHow can I help?\n"

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

            startActivity(emailIntent)
        }
    }