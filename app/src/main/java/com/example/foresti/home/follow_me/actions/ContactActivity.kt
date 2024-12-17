package com.example.foresti.home.follow_me.actions

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.foresti.databinding.ActivityContactBinding
import com.example.foresti.databinding.LayoutAddPeopleFormBinding

class ContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvHeader.setOnClickListener {
            val dialog = Dialog(this)
            val dialogBinding = LayoutAddPeopleFormBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            val window = dialog.window
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            dialogBinding.btnSubmit.setOnClickListener {
                val email = dialogBinding.etEmail.text.toString()
                val name = dialogBinding.etName.text.toString()
                val address = dialogBinding.etAddress.text.toString()
                val phone = dialogBinding.etPhone.text.toString()


                dialog.dismiss()
            }

            dialog.show()
        }
    }
}