package com.belajar.storyapp.view.detail

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.R
import com.belajar.storyapp.databinding.ActivityDetailBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.helper.showLoading
import com.belajar.storyapp.helper.toDateFormat
import com.belajar.storyapp.view.maps.MapsActivity
import com.belajar.storyapp.view.story.StoryActivity
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var latitude: Float? = null
    private var longitude: Float? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.apply {
            title = null
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val viewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: DetailViewModel by viewModels { viewModelFactory }

        val id = intent.getStringExtra(EXTRA_ID)

        if (id != null) {
            viewModel.getDetailStory(id).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Failure -> {
                            Toast.makeText(
                                this@DetailActivity,
                                getString(R.string.error_get_story),
                                Toast.LENGTH_SHORT
                            ).show()
                            showLoading(false, binding.progressBar)
                        }

                        Result.Loading -> {
                            showLoading(true, binding.progressBar)
                        }

                        is Result.Success -> {
                            showLoading(false, binding.progressBar)
                            Glide.with(this)
                                .load(it.data.story?.photoUrl)
                                .into(binding.ivDetailPhoto)
                            binding.apply {
                                tvDetailName.text = it.data.story?.name
                                tvDetailDescription.text = it.data.story?.description
                                tvDetailDate.text = it.data.story?.createdAt?.toDateFormat()
                                latitude = it.data.story?.lat
                                longitude = it.data.story?.lon
                                tvLatLng.text =
                                    String.format(getString(R.string.lat_long), latitude, longitude)
                            }
                            if (latitude != null) {
                                binding.tvLatLng.visibility = View.VISIBLE
                                binding.imgLatLng.visibility = View.VISIBLE
                                binding.tvLatLng.setOnClickListener {
                                    val intent = Intent(this, MapsActivity::class.java)
                                    intent.putExtra(EXTRA_LAT, latitude.toString())
                                    intent.putExtra(EXTRA_LNG, longitude.toString())
                                    val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                                    startActivity(intent, transition)
                                }

                            } else {
                                binding.tvLatLng.text = getString(R.string.no_location)
                            }
                        }
                    }
                }
            }
        }

        binding.btnCta.setOnClickListener {
            val intent = Intent(this, StoryActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@DetailActivity,
                    Pair(binding.imgDetailLogo, "logo"),
                    Pair(binding.tvDetailDescription, "text"),
                    Pair(binding.btnCta, "submit")
                )
            startActivity(intent, optionsCompat.toBundle())
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LNG = "extra_lng"
    }
}