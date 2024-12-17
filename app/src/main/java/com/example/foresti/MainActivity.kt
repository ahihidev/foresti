package com.example.foresti

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foresti.call.CallFragment
import com.example.foresti.databinding.ActivityMainBinding
import com.example.foresti.home.HomeFragment
import com.example.foresti.messenger.MessengerFragment
import com.example.foresti.resource.ResourceFragment
import com.example.foresti.service.VolumeService
import com.example.foresti.victim_voice.VictimVoiceFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment(HomeFragment())
        binding.bottomNavigation.selectedItemId = R.id.menu_home
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_resource -> loadFragment(ResourceFragment())
                R.id.menu_victim_voice -> loadFragment(VictimVoiceFragment())
                R.id.menu_home -> loadFragment(HomeFragment())
                R.id.menu_messenger -> loadFragment(MessengerFragment())
                R.id.menu_call -> loadFragment(CallFragment())
                else -> loadFragment(HomeFragment())
            }
            true
        }
        requestPermissions()
        disableBatteryOptimization()
        startVolumeService()
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
        return true
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.POST_NOTIFICATIONS
                ), 1
            )
        }
    }

    private fun disableBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            val packageName = packageName
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }

    private fun startVolumeService() {
        val serviceIntent = Intent(this, VolumeService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}
