package com.example.foresti.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.foresti.databinding.DialogSetNumberBinding
import com.example.foresti.databinding.DialogSetPhoneNumberBinding
import com.example.foresti.databinding.FragmentHomeBinding
import com.example.foresti.home.follow_me.StartFollowActivity
import com.example.foresti.home.law.LawActivity
import com.example.foresti.home.login.ResgisterActivity
import com.example.foresti.home.share.ShareDocumentActivity
import com.example.foresti.home.test.QuestionActivity
import com.example.foresti.map.MapsActivity
import com.example.foresti.utils.AppPref


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() = binding.run {
        btnSetScreen.setOnClickListener {
            showCustomDialog()
        }
        btnSetPhone.setOnClickListener {
            showPhoneNumberDialog()
        }
        cslSearch.setOnClickListener {
            startActivity(Intent(requireActivity(), MapsActivity::class.java))
        }
        btnShare.setOnClickListener {
            startActivity(Intent(requireActivity(), ShareDocumentActivity::class.java))
        }
        btnLaw.setOnClickListener {
            startActivity(Intent(requireActivity(), LawActivity::class.java))
        }
        btnFollowMe.setOnClickListener {
            startActivity(Intent(requireActivity(), StartFollowActivity::class.java))
        }
        btnTest.setOnClickListener {
            startActivity(Intent(requireActivity(), QuestionActivity::class.java))
        }
        binding.imgAvatar.setOnClickListener {
            startActivity(Intent(requireActivity(), ResgisterActivity::class.java))
        }
    }

    private fun showCustomDialog() {
        val dialogBinding = DialogSetNumberBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireActivity()).setView(dialogBinding.root).create()
        dialogBinding.btnOk.setOnClickListener {
            val inputNumber = dialogBinding.etNumber.text.toString()
            if (inputNumber.isNotEmpty()) {
                Toast.makeText(requireActivity(), "You entered: $inputNumber", Toast.LENGTH_SHORT)
                    .show()
                AppPref.passwordCalculatorScreen = inputNumber
                dialog.dismiss()
            } else {
                Toast.makeText(requireActivity(), "Please enter a number", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialog.show()
    }

    private fun showPhoneNumberDialog() {
        val dialogBinding = DialogSetPhoneNumberBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireActivity()).setView(dialogBinding.root).create()
        dialogBinding.btnOk.setOnClickListener {
            val inputNumber = dialogBinding.etNumber.text.toString()
            if (inputNumber.isNotEmpty()) {
                Toast.makeText(requireActivity(), "You entered: $inputNumber", Toast.LENGTH_SHORT)
                    .show()
                AppPref.phoneNumberService = inputNumber
                dialog.dismiss()
            } else {
                Toast.makeText(requireActivity(), "Please enter a number", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}