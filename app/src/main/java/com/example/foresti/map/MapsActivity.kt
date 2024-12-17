package com.example.foresti.map

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foresti.R
import com.example.foresti.databinding.ActivityMapsBinding
import com.example.foresti.utils.dialog.CustomDialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var editTextLocation: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.btnFind.setOnClickListener {
            val dialog = CustomDialogFragment()
            dialog.show(supportFragmentManager, "CustomDialog")

        }
        binding.btnSuggest.setOnClickListener {
            showCustomDialog()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Lắng nghe sự kiện khi người dùng nhập địa chỉ
        binding.editTextLocation.setOnEditorActionListener { _, actionId, _ ->
            // Kiểm tra nếu hành động là "Enter"
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val location = editTextLocation.text.toString()
                if (location.isNotEmpty()) {
                    searchLocation(location)
                }
                true // Trả về true để ngăn không cho xuống dòng
            } else {
                false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        mMap.isMyLocationEnabled = true

        // Lấy vị trí người dùng
        fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener { location ->
            if (location != null) {
                val userLocation = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
            }
        })
    }

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)

                // Di chuyển camera đến vị trí tìm thấy
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                // Thêm marker vào vị trí tìm thấy
                mMap.addMarker(MarkerOptions().position(latLng).title(locationName))
            } else {
                Toast.makeText(this, "Không tìm thấy địa điểm", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi khi tìm kiếm địa điểm", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_suggest)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val streetButton = dialog.findViewById<LinearLayout>(R.id.btnStreet)
        val nearButton = dialog.findViewById<LinearLayout>(R.id.btnNear)
        val dangerButton = dialog.findViewById<LinearLayout>(R.id.btnDanger)
        val addressText = dialog.findViewById<TextView>(R.id.tv_address)

        streetButton.setOnClickListener {
            showLocationDialog()
        }
        nearButton.setOnClickListener {
            val dialog = CustomDialogFragment()
            dialog.show(supportFragmentManager, "CustomDialog")
        }
        dangerButton.setOnClickListener {
            val dialog = CustomDialogFragment()
            dialog.show(supportFragmentManager, "CustomDialog")
        }

        dialog.show()
    }

    private fun showLocationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_start_street)

        // Thiết lập width cho dialog
        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }

        // Setup RecyclerView
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvSuggestions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample data
        val suggestions = listOf(
            "470 Trần Đại Nghĩa, Ngũ Hành Sơn",
            "470 Trần Đại Nghĩa, Ngũ Hành Sơn",
            "470 Trần Đại Nghĩa, Ngũ Hành Sơn",
            "470 Trần Đại Nghĩa, Ngũ Hành Sơn"
        )

        val adapter = LocationSuggestionAdapter(suggestions) { selectedLocation ->
            // Handle location selection
            dialog.findViewById<EditText>(R.id.etDestination).setText(selectedLocation)
        }
        recyclerView.adapter = adapter

        dialog.show()
    }
}
