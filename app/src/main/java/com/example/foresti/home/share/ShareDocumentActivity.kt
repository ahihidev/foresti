package com.example.foresti.home.share

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.foresti.R
import com.example.foresti.databinding.ActivityShareDocumentBinding
import com.example.foresti.databinding.LayoutAddPeopleFormBinding
import com.google.android.material.tabs.TabLayoutMediator

class ShareDocumentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShareDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initListeners()
    }

    private fun initView() {
        val layouts = listOf(
            R.layout.layout_tab_upload_file,
            R.layout.layout_tab_download_file,
            R.layout.layout_tab_add_people
        )

        binding.viewPager.adapter = ViewPagerAdapter(this, layouts)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Upload File"
                1 -> tab.text = "Download File"
                2 -> tab.text = "Add People"
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.btnAdd.visibility = if (position == 2) View.VISIBLE else View.GONE
            }
        })
    }

    private fun initListeners() {
        binding.btnAdd.setOnClickListener {
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
