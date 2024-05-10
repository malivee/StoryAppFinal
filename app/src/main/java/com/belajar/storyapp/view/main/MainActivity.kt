package com.belajar.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.R
import com.belajar.storyapp.databinding.ActivityMainBinding
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.view.home.HomepageActivity
import com.belajar.storyapp.view.login.LoginActivity
import com.belajar.storyapp.view.register.RegisterActivity
import com.belajar.storyapp.view.story.StoryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val viewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel : MainViewModel by viewModels { viewModelFactory }

        viewModel.getLoginData().observe(this) {

            if (it.isLogin) {
                val intent = Intent(this@MainActivity, HomepageActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        Pair(binding.imgLogo, "logo")
                    )

                startActivity(intent, optionsCompat.toBundle())
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(binding.imgLogo, "logo"),
                    Pair(binding.btnLogin, "login"),
                    Pair(binding.btnGuest, "guest")
                )
            startActivity(intent, optionsCompat.toBundle())
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(binding.imgLogo, "logo"),
                    Pair(binding.btnRegister, "register"),
                    Pair(binding.btnGuest, "guest")
                )
            startActivity(intent, optionsCompat.toBundle())
        }

        binding.btnGuest.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(binding.imgLogo, "logo"),
                    Pair(binding.tvDescription1, "text"),
                    Pair(binding.btnGuest, "submit")
                )
            startActivity(intent, optionsCompat.toBundle())
        }



    }


}