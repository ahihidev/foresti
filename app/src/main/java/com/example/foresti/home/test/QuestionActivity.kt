package com.example.foresti.home.test

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foresti.databinding.ActivityQuestionBinding

class QuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rlRelationship.setOnClickListener {
            startActivity(Intent(this, QuizzActivity::class.java))
            finish()
        }
        binding.rlRelationship2.setOnClickListener {
            startActivity(Intent(this, QuizzActivity::class.java))
            finish()
        }
    }
}