package com.belajar.storyapp.view.setting

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.R
import com.belajar.storyapp.databinding.ActivitySettingBinding
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.view.main.MainActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.apply {
            title = getString(R.string.setting)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val viewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: SettingViewModel by viewModels { viewModelFactory }



        viewModel.getData().observe(this) {
            binding.btnName.text = it.name
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLanguage.setOnClickListener {
            val intent = Intent(Intent(Settings.ACTION_LOCALE_SETTINGS))
            startActivity(intent)
        }

    }

}