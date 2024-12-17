package com.example.foresti.home.follow_me

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foresti.databinding.ActivityFollowMeBinding
import com.example.foresti.utils.AppPref

class FollowMeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFollowMeBinding
    private var timer: CountDownTimer? = null
    private var totalTimeInSeconds: Long = 300 // Mặc định 300 giây (5 phút)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFollowMeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hiển thị thời gian đếm ngược mặc định
        updateTimerText(totalTimeInSeconds)

        // Yêu cầu quyền gọi điện thoại nếu chưa được cấp
        checkCallPermission()

        // Thiết lập sự kiện cho EditText
        binding.etSetTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isNotEmpty()) {
                    totalTimeInSeconds = input.toLongOrNull() ?: 300
                    updateTimerText(totalTimeInSeconds)
                }
            }
        })

        // Thiết lập sự kiện cho nút Finish
        binding.btnFinish.setOnClickListener {
            startCountdown(totalTimeInSeconds)
        }

        // Thiết lập sự kiện cho nút SOS
        binding.btnSOS.setOnClickListener {
            timer?.cancel() // Hủy bỏ đếm ngược
            Toast.makeText(this, "SOS Activated!", Toast.LENGTH_SHORT).show()
            makePhoneCall()
        }
    }

    private fun startCountdown(timeInSeconds: Long) {
        timer?.cancel() // Hủy bỏ nếu đang có một bộ đếm
        timer = object : CountDownTimer(timeInSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                updateTimerText(secondsRemaining)
            }

            override fun onFinish() {
                updateTimerText(0)
                Toast.makeText(this@FollowMeActivity, "Countdown finished!", Toast.LENGTH_SHORT)
                    .show()
                makePhoneCall()
            }
        }
        timer?.start()
    }

    private fun updateTimerText(timeInSeconds: Long) {
        val minutes = timeInSeconds / 60
        val seconds = timeInSeconds % 60
        binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun makePhoneCall() {
        val phoneNumber = AppPref.phoneNumberService
        if (phoneNumber.isNullOrEmpty()) {
            Toast.makeText(this, "No phone number set!", Toast.LENGTH_SHORT).show()
            return
        }

        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        try {
            startActivity(callIntent)
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission denied for making calls", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCallPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel() // Hủy đếm ngược khi Activity bị hủy
    }
}
