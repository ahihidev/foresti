package com.example.foresti.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.foresti.R
import com.example.foresti.utils.AppPref
import kotlin.math.sqrt

class VolumeService : Service(), SensorEventListener {
    private var volumeDownCount = 0
    private val RESET_DELAY = 1000L
    private var lastPressTime = 0L
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "VolumeServiceChannel"
    private val EMERGENCY_NUMBER = AppPref.phoneNumberService

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdate: Long = 0
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f
    private val SHAKE_THRESHOLD = 200
    private var isCallInProgress = false

    // Thêm biến mới cho việc tính toán gia tốc
    private var gravity = FloatArray(3)
    private var linear_acceleration = FloatArray(3)

    private val volumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {
                val currentTime = System.currentTimeMillis()
                println("abc: $volumeDownCount")
                Log.d("VolumeService", "onReceive called")
                if (currentTime - lastPressTime > RESET_DELAY) {
                    volumeDownCount = 0
                }

                if (intent.getIntExtra(
                        "android.media.EXTRA_VOLUME_STREAM_TYPE",
                        -1
                    ) == AudioManager.STREAM_MUSIC
                ) {
                    volumeDownCount++
                    lastPressTime = currentTime

                    if (volumeDownCount == 4) {
                        makeEmergencyCall()
                        volumeDownCount = 0
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundService()
        registerVolumeReceiver()
        initializeSensorManager()
    }

    private fun initializeSensorManager() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Log.e("ShakeDetector", "No accelerometer sensor found")
            return
        }

        val registered = sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME // Đổi sang GAME để có độ nhạy cao hơn
        )

        if (registered) {
            Log.d("ShakeDetector", "Sensor registered successfully")
        } else {
            Log.e("ShakeDetector", "Failed to register sensor")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Tách trọng lực ra khỏi giá trị gia tốc
            val alpha = 0.8f

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

            linear_acceleration[0] = event.values[0] - gravity[0]
            linear_acceleration[1] = event.values[1] - gravity[1]
            linear_acceleration[2] = event.values[2] - gravity[2]

            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastUpdate) > 100) {
                val diffTime = currentTime - lastUpdate
                lastUpdate = currentTime

                // Tính toán độ lắc dựa trên gia tốc tuyến tính
                val speed = sqrt(
                    linear_acceleration[0] * linear_acceleration[0] +
                            linear_acceleration[1] * linear_acceleration[1] +
                            linear_acceleration[2] * linear_acceleration[2]
                )

                Log.d("ShakeDetector", "Linear Speed: $speed")

                if (speed > 8.0 && !isCallInProgress) { // Ngưỡng mới
                    Log.d("ShakeDetector", "Shake detected! Speed: $speed")
                    makeEmergencyCall()
                }

                lastX = event.values[0]
                lastY = event.values[1]
                lastZ = event.values[2]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Volume Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Volume monitoring service"
                setSound(null, null)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Service Running")
            .setContentText("Monitoring volume buttons and shake")
            .setSmallIcon(R.drawable.ic_avatar)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initializeSensorManager() // Đăng ký lại sensor khi service được khởi động lại
        return START_STICKY
    }

    private fun registerVolumeReceiver() {
        val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        registerReceiver(volumeReceiver, filter)
    }

    private fun checkSensorAvailability(): Boolean {
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d("ShakeDetector", "Available sensors: ${deviceSensors.joinToString { it.name }}")
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }

    private fun makeEmergencyCall() {
        if (isCallInProgress) return

        Log.d("VolumeService", "makeEmergencyCall called")
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            isCallInProgress = true
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$EMERGENCY_NUMBER")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            // Reset isCallInProgress after a delay
            android.os.Handler().postDelayed({
                isCallInProgress = false
            }, 5000) // 5 seconds delay
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(volumeReceiver)
        sensorManager.unregisterListener(this)
        val intent = Intent(this, VolumeService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}