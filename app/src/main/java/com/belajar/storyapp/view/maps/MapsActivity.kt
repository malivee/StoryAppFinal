package com.belajar.storyapp.view.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.R
import com.belajar.storyapp.data.api.response.ListStoryItem
import com.belajar.storyapp.databinding.ActivityMapsBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.helper.showLoading
import com.belajar.storyapp.view.detail.DetailActivity
import com.belajar.storyapp.view.home.HomepageActivity
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var latLngBounds = LatLngBounds.Builder()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModelFactory = ViewModelFactory.getInstance(this)
    private val mapsViewModel: MapsViewModel by viewModels { viewModelFactory }

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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        val latDetail = intent?.getStringExtra(DetailActivity.EXTRA_LAT)
        val lngDetail = intent.getStringExtra(DetailActivity.EXTRA_LNG)
        val storiesMap = intent?.getStringExtra(HomepageActivity.EXTRA_MAP)

        if (latDetail != null) {
            binding.hintOverlay.tvHint.text = getString(R.string.story_location)
            val location = lngDetail?.toDouble()?.let { LatLng(latDetail.toDouble(), it) }
            location?.let {
                MarkerOptions().position(it).title(getString(R.string.current_location))
            }
                ?.let { mMap.addMarker(it) }
            location?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                ?.let { mMap.animateCamera(it) }


        } else if (storiesMap != null) {
            getMapStories()
            binding.hintOverlay.tvHint.text = getString(R.string.posted_location)


        } else {
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
                            snippet(
                                String.format(
                                    getString(R.string.lat_long),
                                    it.latLng.latitude,
                                    it.latLng.longitude
                                )
                            )
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
                            title(
                                String.format(
                                    getString(R.string.marker),
                                    it.latitude,
                                    it.longitude
                                )
                            )
                            snippet(
                                String.format(
                                    getString(R.string.lat_long),
                                    it.latitude,
                                    it.longitude
                                )
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

        setupMapStyle()
    }

    private fun getMapStories() {
        mapsViewModel.getMapStories().observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Failure -> {
                        showLoading(false, binding.progressBar)
                        Toast.makeText(this, getString(R.string.map_error), Toast.LENGTH_SHORT)
                            .show()
                    }

                    Result.Loading -> {
                        showLoading(true, binding.progressBar)
                    }

                    is Result.Success -> {
                        showLoading(false, binding.progressBar)
                        it.data.listStory?.forEach { item ->
                            val location = item.lat?.let { lat ->
                                item.lon?.let { lon ->
                                    LatLng(
                                        lat,
                                        lon
                                    )
                                }
                            }

                            val markerOptions = MarkerOptions().apply {
                                title(item.name)
                                if (location != null) {
                                    position(location)
                                    latLngBounds.include(location)
                                }

                                snippet(
                                    String.format(
                                        getString(R.string.lat_long),
                                        item.lat,
                                        item.lon
                                    )
                                )

                                icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_CYAN
                                    )
                                )

                                mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngBounds(
                                        latLngBounds.build(),
                                        100
                                    )
                                )

                            }

                            marker = mMap.addMarker(markerOptions)
                            marker?.tag = item

                            mMap.setOnMarkerClickListener { markedClick ->
                                val items = markedClick.tag as ListStoryItem
                                val itemClickLocation = LatLng(items.lat!!, items.lon!!)
                                binding.userDetail.root.visibility = View.VISIBLE
                                binding.userDetail.tvName.text = items.name
                                binding.userDetail.tvDesc.text = items.description
                                Glide.with(this@MapsActivity)
                                    .load(items.photoUrl)
                                    .into(binding.userDetail.ivPhoto)
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(itemClickLocation))
                                true
                            }

                            mMap.setOnMapClickListener {
                                binding.userDetail.root.visibility = View.GONE
                            }

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
                        markCurrentLocation(it)
                        true

                    }
                    mMap.setOnMyLocationClickListener {
                        markCurrentLocation(it)
                    }
                    markCurrentLocation(it)


                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.error_map_access),
                        Toast.LENGTH_SHORT
                    ).show()
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
                title(getString(R.string.current_location))
                snippet(
                    String.format(
                        getString(R.string.lat_long),
                        location.latitude,
                        location.longitude
                    )
                )
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