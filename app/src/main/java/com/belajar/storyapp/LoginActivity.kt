package com.belajar.storyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.databinding.ActivityLoginBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.helper.isValidEmail

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

        val viewModelFactory = ViewModelFactory.getInstance(this@LoginActivity)
        val viewModel: LoginViewModel by viewModels { viewModelFactory }

        binding.root.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.btnRegisterHere.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogLogin.setOnClickListener {
            val email = binding.emailEditLog.text.toString()
            val password = binding.passwordEditLog.text.toString()
            viewModel.postLogin(email, password).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Failure -> {
                            Toast.makeText(this@LoginActivity, it.error, Toast.LENGTH_SHORT).show()
                        }
                        Result.Loading -> {}
                        is Result.Success -> {
                            val token = it.data.loginResult?.token.toString()
                            val name = it.data.loginResult?.name.toString()
                            viewModel.saveData(DataModel(
                                name,
                                token
                            ))
                            val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
                            intent.putExtra(EXTRA_RESULT, it.data.loginResult?.token.toString())
                            intent.putExtra(EXTRA_NAME, it.data.loginResult?.token.toString())
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            Log.i("TOKEN", token)
                            startActivity(intent)

                        }
                    }
                }
            }


        }

    }

    private fun buttonDisabled() {
        val emailDisable = !isValidEmail(binding.emailEditLog.text.toString())
        val passwordDisable = binding.passwordEditLog.text.toString().length <= 8

        if (!emailDisable && !passwordDisable) {
            binding.btnLogLogin.isEnabled = true
            binding.btnLogLogin.setBackgroundColor(getColor(R.color.dark_blue))
            return

        } else {
            binding.btnLogLogin.isEnabled = false
            binding.btnLogLogin.setBackgroundColor(getColor(R.color.dark_blue_disabled))
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
        binding.emailEditLog.addTextChangedListener(textWatcher)
        binding.passwordEditLog.addTextChangedListener(textWatcher)

    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_NAME = "extra_name"
    }
}