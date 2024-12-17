package com.example.foresti.home.follow_me

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foresti.databinding.ActivityStartFollowBinding
import com.example.foresti.home.follow_me.actions.ContactActivity
import com.example.foresti.home.follow_me.actions.CountDownActivity
import com.example.foresti.home.follow_me.actions.RunningActivity
import com.example.foresti.map.MapsActivity

class StartFollowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartFollowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStartFollowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()

    }

    private fun initListeners() = binding.run {
        btnStartActivity.setOnClickListener {
            val intent = Intent(this@StartFollowActivity, FollowMeActivity::class.java)
            startActivity(intent)
        }
        btnRun.setOnClickListener {
            val intent = Intent(this@StartFollowActivity, RunningActivity::class.java)
            startActivity(intent)
        }
        btnCountdown.setOnClickListener {
            val intent = Intent(this@StartFollowActivity, CountDownActivity::class.java)
            startActivity(intent)
        }
        btnContact.setOnClickListener {
            val intent = Intent(this@StartFollowActivity, ContactActivity::class.java)
            startActivity(intent)
        }
        btnTravel.setOnClickListener {
            val intent = Intent(this@StartFollowActivity, MapsActivity::class.java)
            startActivity(intent)
        }

    }

}