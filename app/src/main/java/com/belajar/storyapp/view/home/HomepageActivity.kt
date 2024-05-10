package com.belajar.storyapp.view.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.belajar.storyapp.R
import com.belajar.storyapp.databinding.ActivityHomepageBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.helper.showLoading
import com.belajar.storyapp.view.maps.MapsActivity
import com.belajar.storyapp.view.setting.SettingActivity
import com.belajar.storyapp.view.story.StoryActivity

class HomepageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding

    private val viewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: HomepageViewModel by viewModels { viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.apply {
            title = null
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val adapter = HomeAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        binding.rvStory.layoutManager = layoutManager


        viewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
//            if (it != null) {
//                when (it) {
//                    is Result.Failure -> {
//                        showLoading(false, binding.progressBar)
//                        Toast.makeText(
//                            this@HomepageActivity,
//                            getString(R.string.error_get_story),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    Result.Loading -> {
//                        showLoading(true, binding.progressBar)
//                    }
//
//                    is Result.Success -> {
//                        showLoading(false, binding.progressBar)
//                        it.let {
//                            adapter.submitList(it.data.listStory.orEmpty().mapNotNull { it })
//
//                        }
//                    }
//
//                }
//            }

        }

        viewModel.getLoginData().observe(this) {
            val loginState = it.isLogin
            val token = it.token
            Log.d("LOGINSTATEHOME", loginState.toString())
            Log.d("TOKENSTATEHOME", token.toString())
        }



        binding.btnStory.setOnClickListener {
            val intent = Intent(this@HomepageActivity, StoryActivity::class.java)
            val optionsCompact: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@HomepageActivity,
                    Pair(binding.btnStory, "text"),
                    Pair(binding.imgLogo, "logo")
                )
            startActivity(intent, optionsCompact.toBundle())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btn_setting) {
            val intent = Intent(this, SettingActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@HomepageActivity,
                    Pair(binding.btnStory, "name"),
                    Pair(binding.imgAvatar, "avatar"),
                    Pair(binding.imgLogo, "logo")
                )
            startActivity(intent, optionsCompat.toBundle())
        }
        if (item.itemId == R.id.btn_map_option) {
            val intent = Intent(this, MapsActivity::class.java)
            val mapString = "mapString"
            intent.putExtra(EXTRA_MAP, mapString)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_MAP ="extra_map"
    }

}