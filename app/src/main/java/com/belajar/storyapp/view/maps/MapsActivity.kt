package com.belajar.storyapp.view.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.belajar.storyapp.databinding.ActivityMapsBinding
import com.belajar.storyapp.view.camera.CameraActivity
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var latLngBounds: LatLngBounds
    private var marker: Marker? = null
    private var latLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isCompassEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true

//        if (latLng == null) {
//            latLng = LatLng(mMap.myLocation.latitude, mMap.myLocation.longitude)
//            mMap.addMarker(MarkerOptions().position(latLng!!).title("Current Location"))
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 15f))
//        }
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))




        mMap.setOnPoiClickListener {
            if (marker != null) {
                markerReset()
            } else {
                marker = mMap.addMarker(
                    MarkerOptions().apply {
                        position(it.latLng)
                        title(it.name)
                        snippet(String.format(getString(R.string.lat_long), it.latLng.latitude, it.latLng.longitude)
                        )

                        binding.overlayMap.root.visibility = View.VISIBLE
                        binding.overlayMap.tvLocationName.text = title
                        binding.overlayMap.tvCoordinate.text = snippet

                        binding.overlayMap.btnConfirm.setOnClickListener { _ ->
                            val intent = Intent()
                            intent.putExtra(EXTRA_LAT, it.latLng.latitude.toString())
                            intent.putExtra(EXTRA_LNG, it.latLng.longitude.toString())
                            setResult(EXTRA_RESULT, intent)
                            finish()
                        }
                    }
                )
            }



        }

        mMap.setOnMapClickListener {
            if (marker != null) {
                markerReset()
            } else {
                marker = mMap.addMarker(
                    MarkerOptions().apply {
                        position(it)
                        title(String.format(getString(R.string.marker), it.latitude, it.longitude))
                        snippet(String.format(getString(R.string.lat_long), it.latitude, it.longitude)
                        )
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))

                        binding.overlayMap.tvCoordinate.text = snippet
                        binding.overlayMap.tvLocationName.text = title
                        binding.overlayMap.root.visibility = View.VISIBLE

                        binding.overlayMap.btnConfirm.setOnClickListener { _ ->
                            val intent = Intent()
                            intent.putExtra(EXTRA_LAT, it.latitude.toString())
                            intent.putExtra(EXTRA_LNG, it.longitude.toString())
                            setResult(EXTRA_RESULT, intent)
                            finish()
                        }
                    }
                )
            }
        }

        mMap.setOnMyLocationButtonClickListener {
            val location = mMap.myLocation
            if (location != null) {
                val latitude = mMap.myLocation.latitude
                Log.d("LATITUDE", latitude.toString())
                val longitude = mMap.myLocation.longitude
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15f))

                binding.overlayMap.root.visibility = View.VISIBLE
                binding.overlayMap.tvLocationName.text =
                    String.format(getString(R.string.position), latitude, longitude)
                binding.overlayMap.tvCoordinate.text =
                    String.format(getString(R.string.lat_long), latitude, longitude)

                binding.overlayMap.btnConfirm.setOnClickListener { _ ->
                    val intent = Intent()
                    intent.putExtra(EXTRA_LAT, latitude)
                    intent.putExtra(EXTRA_LNG, longitude)
                    setResult(EXTRA_RESULT, intent)
                    finish()
                }
            }




            true
        }

        setupMapStyle()
//        getMyCurrentLocation()
    }

//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) {
//        if (it) {
//            getMyCurrentLocation()
//        }
//    }
//
//    private fun getMyCurrentLocation() {
//        if (ContextCompat.checkSelfPermission(
//                this.applicationContext,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            mMap.isMyLocationEnabled = true
//        } else {
//            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }


    private fun setupMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Styling failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun markerReset() {
        marker?.remove()
        marker = null
        binding.overlayMap.root.visibility = View.GONE
    }

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LNG = "extra_lng"
        const val EXTRA_RESULT = 110
    }

}