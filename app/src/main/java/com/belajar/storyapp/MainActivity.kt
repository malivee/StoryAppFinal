package com.belajar.storyapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.databinding.ActivityMainBinding
import com.belajar.storyapp.helper.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            val loginState = it.isLogin
            val token = it.token
            val name = it.name
            Log.d("LOGINSTATE", loginState.toString())
            Log.d("TOKENSTATE", token.toString())

            if (it.isLogin) {
                val intent = Intent(this@MainActivity, HomepageActivity::class.java)
                intent.putExtra(EXTRA_TOKEN, token)
                intent.putExtra(EXTRA_NAME_MAIN, name)
                Log.d("TOKENMAIN", token.toString())
                startActivity(intent)
            }
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }



    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        const val EXTRA_NAME_MAIN = "extra_name_main"
    }
}