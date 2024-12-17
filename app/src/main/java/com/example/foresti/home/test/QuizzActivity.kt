package com.example.foresti.home.test

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foresti.databinding.ActivityQuizzBinding

class QuizzActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizzBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQuizzBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSubmit.setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
            finish()
        }

    }
}