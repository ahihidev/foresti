package com.example.foresti.home.law

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.foresti.databinding.ActivityLawBinding

class LawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLawBinding
    private val lawAdapter = LawAdapter { law ->
        // Navigate to detail screen
        startActivity(LawDetailActivity.createIntent(this, law))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLawBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        loadFakeData()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = lawAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@LawActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun loadFakeData() {
        val fakeData = listOf(
            Law(1, "Constitutional Law", "Fundamental principles of governance", 5),
            Law(2, "Criminal Law", "Rules for criminal offenses", 3),
            Law(3, "Civil Law", "Private rights and remedies", 2),
            Law(4, "Administrative Law", "Government agency regulations", 4),
            Law(5, "International Law", "Rules between nations", 4)
        )
        lawAdapter.submitList(fakeData)
    }
}