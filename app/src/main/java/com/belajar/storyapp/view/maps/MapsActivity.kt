package com.belajar.storyapp.view.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.belajar.storyapp.R
import com.belajar.storyapp.data.api.response.AllStoryResponse
import com.belajar.storyapp.data.api.response.ListStoryItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.belajar.storyapp.databinding.ActivityMapsBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.helper.showLoading
import com.belajar.storyapp.view.camera.CameraActivity
import com.belajar.storyapp.view.detail.DetailActivity
import com.belajar.storyapp.view.home.HomepageActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var latLngBounds =  LatLngBounds.Builder()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModelFactory = ViewModelFactory.getInstance(this)
    private val mapsViewModel: MapsViewModel by viewModels { viewModelFactory }

    private val arrayLatLng = ArrayList<ListStoryItem>()

    private var marker: Marker? = null


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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

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
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
        mMap.uiSettings.isMyLocationButtonEnabled = true
//        mMap.isMyLocationEnabled = true

//        if (latLng == null) {
//            latLng = LatLng(mMap.myLocation.latitude, mMap.myLocation.longitude)
//            mMap.addMarker(MarkerOptions().position(latLng!!).title("Current Location"))
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 15f))
//        }
        val latDetail = intent?.getStringExtra(DetailActivity.EXTRA_LAT)
        val lngDetail = intent.getStringExtra(DetailActivity.EXTRA_LNG)
        val storiesMap = intent?.getStringExtra(HomepageActivity.EXTRA_MAP)
        if (latDetail != null) {
            binding.hintOverlay.root.visibility = View.GONE
            val location = lngDetail?.toDouble()?.let { LatLng(latDetail.toDouble(), it) }
            location?.let { MarkerOptions().position(it).title("Current Location") }
                ?.let { mMap.addMarker(it) }
            location?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                ?.let { mMap.animateCamera(it) }
        } else if (storiesMap != null) {
            getMapStories()
            binding.hintOverlay.tvHint.text = "Location of posted stories"
        } else {
//            val sydney = LatLng(-34.0, 151.0)
//            mMap.addMarker(MarkerOptions().position(sydney).title("Current Location"))
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
            binding.hintOverlay.root.visibility = View.VISIBLE

            getMyCurrentLocation()

            mMap.setOnPoiClickListener {
                if (marker != null) {
                    markerReset()
                } else {
                    marker = mMap.addMarker(
                        MarkerOptions().apply {
                            position(it.latLng)
                            title(it.name)
                            snippet(String.format(getString(R.string.lat_long), it.latLng.latitude, it.latLng.longitude))
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))


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
        }






//        mMap.setOnMyLocationButtonClickListener {
//            val location = getMyCurrentLocation().apply
//            if (location != null) {
//                val latitude = mMap.myLocation.latitude
//                Log.d("LATITUDE", latitude.toString())
//                val longitude = mMap.myLocation.longitude
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15f))
//
//                binding.overlayMap.root.visibility = View.VISIBLE
//                binding.overlayMap.tvLocationName.text =
//                    String.format(getString(R.string.position), latitude, longitude)
//                binding.overlayMap.tvCoordinate.text =
//                    String.format(getString(R.string.lat_long), latitude, longitude)
//
//                binding.overlayMap.btnConfirm.setOnClickListener { _ ->
//                    val intent = Intent()
//                    intent.putExtra(EXTRA_LAT, latitude)
//                    intent.putExtra(EXTRA_LNG, longitude)
//                    setResult(EXTRA_RESULT, intent)
//                    finish()
//                }
//            }
//
//
//
//
//            true
//        }

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

//    private val requestLocationPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) {
//        if (it) {
//            Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun getMapStories() {
        mapsViewModel.getMapStories().observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Failure -> {
                        showLoading(false, binding.progressBar)
                        Toast.makeText(this, "Failed to show the result, please try again", Toast.LENGTH_SHORT).show()

                    }
                    Result.Loading -> {
                        showLoading(true, binding.progressBar)
                    }
                    is Result.Success -> {
                        showLoading(true, binding.progressBar)
                        it.data.listStory?.forEach { item ->
                            val location = item.lat?.let { lat ->
                                item.lon?.let { lon ->
                                    LatLng(
                                        lat,
                                        lon
                                    )
                            } }
                            mMap.addMarker(
                                MarkerOptions().apply {
                                  title(item.name)
                                    if (location != null) {
                                        position(location)
                                        latLngBounds.include(location)

                                    }
                                    snippet(String.format(getString(R.string.lat_long), item.lat, item.lon))
                                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 64))

                                })


                        }


                    }
                }
            }
        }
    }


    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        when {
            it[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyCurrentLocation()
            }

            it[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyCurrentLocation()
            }

            else -> {
            }
        }
    }

    private fun getMyCurrentLocation() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    if (it != null) {
                        mMap.setOnMyLocationButtonClickListener {
//                                MarkerOptions().apply {
//                                    position(location)
//                                    title("Current Location")
//                                    snippet(String.format(getString(R.string.lat_long), it.latitude, it.longitude))
//                                    binding.overlayMap.tvCoordinate.text = snippet
//                                    binding.overlayMap.tvLocationName.text = title
//                                    binding.overlayMap.root.visibility = View.VISIBLE
//                                }
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                            markCurrentLocation(it)
                            true

                        }
                        mMap.setOnMyLocationClickListener {
//                            val location = LatLng(it.latitude, it.longitude)
                            markCurrentLocation(it)
                        }
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                        markCurrentLocation(it)



                    } else {
                        Toast.makeText(this, "Cannot Access Location, please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                requestLocationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
    }

    private fun markCurrentLocation(loc: Location) {
        val location = LatLng(loc.latitude, loc.longitude)
        mMap.addMarker(
            MarkerOptions().apply {
                position(location)
                title("Current Location")
                snippet(String.format(getString(R.string.lat_long), location.latitude, location.longitude))
                binding.overlayMap.tvCoordinate.text = snippet
                binding.overlayMap.tvLocationName.text = title
                binding.overlayMap.root.visibility = View.VISIBLE

                binding.overlayMap.btnConfirm.setOnClickListener {
                    val intent = Intent()
                    intent.putExtra(EXTRA_LAT, location.latitude.toString())
                    intent.putExtra(EXTRA_LNG, location.longitude.toString())
                    setResult(EXTRA_RESULT, intent)
                    finish()
                }
            }
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }


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