package com.belajar.storyapp

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.databinding.ActivityRegisterBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.helper.isValidEmail
import com.belajar.storyapp.helper.showLoading

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var isDisabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: RegisterViewModel by viewModels { viewModelFactory }

        setupInputListener()

        buttonDisabled()

        binding.main.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.btnRegGuest.setOnClickListener {
            val intent = Intent(this, StoryActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@RegisterActivity,
                    Pair(binding.imgLogo, "logo"),
                    Pair(binding.edRegPassword, "text"),
                    Pair(binding.btnRegGuest, "submit")
                )
            startActivity(intent, optionsCompat.toBundle())
        }

        binding.btnRegRegister.setOnClickListener {
            viewModel.postRegister(
                binding.nameEditReg.text.toString(),
                binding.emailEditReg.text.toString(),
                binding.passwordEditReg.text.toString()
            ).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Failure -> {
                            binding.emailEditReg.setBackgroundResource(R.drawable.text_edit_error)
                            binding.passwordEditReg.setBackgroundResource(R.drawable.text_edit_error)
                            binding.tvError.visibility = View.VISIBLE
                            showLoading(false, binding.progressBar)

                        }

                        Result.Loading -> {
                            showLoading(true, binding.progressBar)

                        }
                        is Result.Success -> {
                            showLoading(false, binding.progressBar)
                            buttonDisabled()
                            Toast.makeText(
                                this@RegisterActivity,
                                getString(R.string.register_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            val optionsCompact: ActivityOptionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    this@RegisterActivity,
                                    Pair(binding.btnRegRegister, "register"),
                                    Pair(binding.btnRegGuest, "guest"),
                                    Pair(binding.tvRegTitle, "title"),
                                    Pair(binding.tvRegDesc, "description"),
                                    Pair(binding.tvRegEmail, "email"),
                                    Pair(binding.edRegEmail, "email2"),
                                    Pair(binding.tvRegPassword, "password"),
                                    Pair(binding.edRegPassword, "password2")
                                )
                            startActivity(intent, optionsCompact.toBundle())
                        }
                    }
                }
            }
        }





        binding.main.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.btnLoginHere.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            val optionsCompact: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@RegisterActivity,
                    Pair(binding.btnRegRegister, "register"),
                    Pair(binding.btnRegGuest, "guest"),
                    Pair(binding.tvRegTitle, "title"),
                    Pair(binding.tvRegDesc, "description"),
                    Pair(binding.tvRegEmail, "email"),
                    Pair(binding.edRegEmail, "email2"),
                    Pair(binding.tvRegPassword, "password"),
                    Pair(binding.edRegPassword, "password2"),
                    Pair(binding.tvLoginHere, "account1"),
                    Pair(binding.btnLoginHere, "account2")
                )
            startActivity(intent, optionsCompact.toBundle())
        }
    }

    private fun buttonDisabled() {
        val emailDisable = !isValidEmail(binding.emailEditReg.text.toString())
        val nameDisable = binding.nameEditReg.text.toString().isEmpty()
        val passwordDisable = binding.passwordEditReg.text.toString().length <= 8

        if (!emailDisable && !nameDisable && !passwordDisable) {
            binding.btnRegRegister.isEnabled = true
            binding.btnRegRegister.setBackgroundColor(getColor(R.color.dark_blue))
            return
        } else {
            binding.btnRegRegister.isEnabled = false
            binding.btnRegRegister.setBackgroundColor(getColor(R.color.dark_blue_disabled))
            binding.btnRegRegister.setTextColor(getColor(R.color.white))
            return

        }
    }

//    private fun buttonDisabled() {
//        val email = binding.emailEditReg.text.toString()
//        val name = binding.nameEditReg.text.toString()
//        val password = binding.passwordEditReg.text.toString()
//
//        val isValidEmail = isValidEmail(email)
//        val isNameEmpty = name.isEmpty()
//        val isPasswordValid = password.length >= 8
//
//        val isButtonEnabled = isValidEmail && !isNameEmpty && isPasswordValid
//
//        binding.btnRegRegister.isEnabled = isButtonEnabled
//        binding.btnRegRegister.setBackgroundColor(getColor(if (isButtonEnabled) R.color.dark_blue else R.color.dark_blue_disabled))
//    }

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
        binding.emailEditReg.addTextChangedListener(textWatcher)
        binding.passwordEditReg.addTextChangedListener(textWatcher)
        binding.nameEditReg.addTextChangedListener(textWatcher)


    }

}