package com.belajar.storyapp

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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.belajar.storyapp.databinding.ActivityHomepageBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory

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
        binding.rvStory.adapter = adapter
        binding.rvStory.layoutManager = layoutManager

//        val token = intent.getStringExtra(LoginActivity.EXTRA_RESULT)
//        val token = viewModel.getLoginData().observe(this) {
//            it.token
//        }
//        val tokenGet = token.toString()
//        val tokenFromMain = intent.getStringExtra(MainActivity.EXTRA_TOKEN)
//        val name = intent.getStringExtra(LoginActivity.EXTRA_NAME)
//        val nameMain = intent.getStringExtra(MainActivity.EXTRA_NAME_MAIN)
//        Log.i("TOKENHOME", tokenFromMain.toString())


        viewModel.getStories().observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Failure -> Toast.makeText(
                        this@HomepageActivity,
                        it.error,
                        Toast.LENGTH_SHORT
                    ).show()

                    Result.Loading -> {}
                    is Result.Success -> {
                        it.let {
                            adapter.submitList(it.data.listStory.orEmpty().mapNotNull { it })

                        }
                    }

                }
            }

        }
//        Log.d("ViewDebug","$viewDebug.")

        viewModel.getLoginData().observe(this) {
            val loginState = it.isLogin
            val token = it.token
            Log.d("LOGINSTATEHOME", loginState.toString())
            Log.d("TOKENSTATEHOME", token.toString())
        }



        binding.btnStory.setOnClickListener {
            val intent = Intent(this@HomepageActivity, StoryActivity::class.java)
//            intent.putExtra(EXTRA_NAME, name ?: nameMain)
//            intent.putExtra(EXTRA_TOKEN, token ?: tokenFromMain)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btn_setting) {
            viewModel.logout()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
        return super.onOptionsItemSelected(item)
    }

//    companion object {
//        const val EXTRA_NAME = "name_extra"
//        const val EXTRA_TOKEN = "token_extra"
//    }

}