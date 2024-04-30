package com.belajar.storyapp.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.R
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.databinding.ActivityLoginBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.helper.isValidEmail
import com.belajar.storyapp.helper.showLoading
import com.belajar.storyapp.view.home.HomepageActivity
import com.belajar.storyapp.view.register.RegisterActivity
import com.belajar.storyapp.view.story.StoryActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupInputListener()

        buttonDisabled()

        val viewModelFactory = ViewModelFactory.getInstance(this@LoginActivity)
        val viewModel: LoginViewModel by viewModels { viewModelFactory }

        binding.main.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.btnRegisterHere.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            val optionsCompact: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@LoginActivity,
                    Pair(binding.btnLogLogin, "login"),
                    Pair(binding.btnGuest, "guest"),
                    Pair(binding.tvLogTitle, "title"),
                    Pair(binding.tvLogDesc, "description"),
                    Pair(binding.tvLogEmail, "email"),
                    Pair(binding.edEmail, "email2"),
                    Pair(binding.tvLogPassword, "password"),
                    Pair(binding.edPassword, "password2"),
                    Pair(binding.tvRegisterHere, "account1"),
                    Pair(binding.btnRegisterHere, "account2")
                )
            startActivity(intent, optionsCompact.toBundle())
        }

        binding.btnGuest.setOnClickListener {
            val intent = Intent(this@LoginActivity, StoryActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@LoginActivity,
                    Pair(binding.imgLogo, "logo"),
                    Pair(binding.edPassword, "text"),
                    Pair(binding.btnGuest, "submit")
                )
            startActivity(intent, optionsCompat.toBundle())
        }

        binding.btnLogLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            viewModel.postLogin(email, password).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Failure -> {
                            binding.edLoginEmail.setBackgroundResource(R.drawable.text_edit_error)
                            binding.edLoginPassword.setBackgroundResource(R.drawable.text_edit_error)
                            binding.tvError.visibility = View.VISIBLE
                            showLoading(false, binding.progressBar)
                        }

                        Result.Loading -> {
                            showLoading(true, binding.progressBar)
                        }

                        is Result.Success -> {
                            showLoading(false, binding.progressBar)
                            val token = it.data.loginResult?.token.toString()
                            val name = it.data.loginResult?.name.toString()
                            viewModel.saveData(
                                DataModel(
                                    name,
                                    token
                                )
                            )
                            val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            Log.i("TOKEN", token)
                            val optionsCompat: ActivityOptionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    this@LoginActivity,
                                    Pair(binding.imgLogo, "logo")
                                )
                            startActivity(intent, optionsCompat.toBundle())
                            finish()
                        }
                    }
                }
            }


        }

    }

    private fun buttonDisabled() {
        val emailDisable = !isValidEmail(binding.edLoginEmail.text.toString())
        val passwordDisable = binding.edLoginPassword.text.toString().length < 8

        if (!emailDisable && !passwordDisable) {
            binding.btnLogLogin.isEnabled = true
            binding.btnLogLogin.setBackgroundColor(getColor(R.color.dark_blue))
            return

        } else {
            binding.btnLogLogin.isEnabled = false
            binding.btnLogLogin.setBackgroundColor(getColor(R.color.dark_blue_disabled))
            binding.btnLogLogin.setTextColor(getColor(R.color.white))
            return
        }
    }

    private fun setupInputListener() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonDisabled()
            }
        }
        binding.edLoginEmail.addTextChangedListener(textWatcher)
        binding.edLoginPassword.addTextChangedListener(textWatcher)

    }
}