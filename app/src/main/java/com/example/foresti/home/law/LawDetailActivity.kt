package com.example.foresti.home.law

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foresti.databinding.ActivityLawDetailBinding

class LawDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLawDetailBinding

    companion object {
        private const val EXTRA_LAW = "extra_law"

        fun createIntent(context: Context, law: Law): Intent {
            return Intent(context, LawDetailActivity::class.java).apply {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLawDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}