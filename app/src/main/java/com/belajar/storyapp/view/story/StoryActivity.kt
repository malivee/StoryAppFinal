package com.belajar.storyapp.view.story

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.belajar.storyapp.R
import com.belajar.storyapp.databinding.ActivityStoryBinding
import com.belajar.storyapp.helper.Result
import com.belajar.storyapp.helper.ViewModelFactory
import com.belajar.storyapp.helper.reduceFileImage
import com.belajar.storyapp.helper.showLoading
import com.belajar.storyapp.helper.uriToFile
import com.belajar.storyapp.view.camera.CameraActivity
import com.belajar.storyapp.view.home.HomepageActivity
import com.belajar.storyapp.view.main.MainActivity
import com.belajar.storyapp.view.setting.SettingActivity
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private var currentImageUri: Uri? = null

    private val viewModelFactory = ViewModelFactory.getInstance(this@StoryActivity)
    private val viewModel: StoryViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        supportActionBar?.apply {
            title = null
            viewModel.getLoginData().observe(this@StoryActivity) {
                if (!it.isLogin) {
                    setHomeButtonEnabled(false)
                    setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                } else {
                    setHomeButtonEnabled(true)
                    setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
//            setBackgroundDrawable(ColorDrawable(getColor(R.color.dark_blue)))
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }

        binding.main.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCamera.setOnClickListener {
            if (!cameraPermission()) {
                permissionLauncher.launch(REQUIRED_PERMISSION)
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                cameraXLauncher.launch(intent)
            }
        }

        binding.buttonAdd.setOnClickListener {
            viewModel.getLoginData().observe(this) {
                if (it.isLogin) {
                    postStory()
                } else {
                    postStoryGuest()
                }
            }
        }

    }


    private fun postStory() {
        currentImageUri.let {
            val imageFile = it?.let { uri -> uriToFile(uri, this@StoryActivity).reduceFileImage() }
            val description = binding.edAddDescription.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile?.asRequestBody("image/jpeg".toMediaType())

            val multipartBody = requestImageFile?.let { imageFile ->
                MultipartBody.Part.createFormData(
                    "photo",
                    description,
                    imageFile
                )
            }

            if (multipartBody != null) {
                viewModel.postStory(multipartBody, requestBody).observe(this) {
                    if (it != null) {
                        when (it) {
                            is Result.Failure -> {
                                Toast.makeText(
                                    this@StoryActivity,
                                    getString(R.string.error_post_story),
                                    Toast.LENGTH_SHORT
                                ).show()
                                showLoading(false, binding.progressBar)
                            }

                            Result.Loading -> {
                                showLoading(true, binding.progressBar)
                            }

                            is Result.Success -> {
                                showLoading(false, binding.progressBar)
                                Toast.makeText(
                                    this@StoryActivity,
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, HomepageActivity::class.java)
                                val optionsCompat: ActivityOptionsCompat =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@StoryActivity,
                                        Pair(binding.imgStoryPhoto, "logo"),
                                        Pair(binding.edAddDescription, "text")
                                    )
                                startActivity(intent, optionsCompat.toBundle())
                                finish()
                            }
                        }
                    }
                }
            }


        }
    }


    private fun postStoryGuest() {
        currentImageUri?.let {
            val imageFile = it?.let { uri -> uriToFile(uri, this@StoryActivity) }
            val description = binding.edAddDescription.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile?.asRequestBody("image/jpeg".toMediaType())

            val multipartBody = requestImageFile?.let { imageFile ->
                MultipartBody.Part.createFormData(
                    "photo",
                    description,
                    imageFile
                )
            }
            if (multipartBody != null) {
                viewModel.postStoryGuest(multipartBody, requestBody).observe(this@StoryActivity) {
                    if (it != null) {
                        when (it) {
                            is Result.Failure -> {
                                Toast.makeText(
                                    this@StoryActivity,
                                    getString(R.string.error_post_story),
                                    Toast.LENGTH_SHORT
                                ).show()
                                showLoading(false, binding.progressBar)
                            }

                            Result.Loading -> {
                                showLoading(true, binding.progressBar)
                            }

                            is Result.Success -> {
                                showLoading(false, binding.progressBar)
                                val intent =
                                    Intent(this@StoryActivity, MainActivity::class.java)
                                val optionsCompat: ActivityOptionsCompat =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@StoryActivity,
                                        Pair(binding.imgStoryPhoto, "logo")
                                    )
                                startActivity(intent, optionsCompat.toBundle())
                                finish()
                                Toast.makeText(
                                    this@StoryActivity,
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cameraPermission() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraXLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.EXTRA_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_URI)?.toUri()
            showImage()
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        it?.let {
            currentImageUri = it
            showImage()
        }
    }

    private fun cropImage(imageUri: Uri): Intent {
        val destinationUri = Uri.fromFile(uriToFile(imageUri, this@StoryActivity))
        val options = UCrop.Options().apply {
            setCompressionQuality(90)
            setFreeStyleCropEnabled(true)
        }
        val intent = UCrop.of(imageUri, destinationUri)
            .withOptions(options)
            .getIntent(this)

        cropImageLauncher.launch(intent)
        return intent
    }


    private val cropImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val result = UCrop.getOutput(it.data!!)
            if (result != null) {
                val bitmap = BitmapFactory.decodeFile(result.encodedSchemeSpecificPart)
                binding.imgStoryPhoto.setImageBitmap(bitmap)
                currentImageUri = result
            }

        } else if (it.resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(it.data!!)
            Log.e("StoryActivity", error.toString())
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            cropImage(it)
            binding.imgStoryPhoto.setImageURI(it)
            Log.d("ImageUri", "showImage: $it")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        viewModel.getLoginData().observe(this@StoryActivity) {
            if (it.isLogin) {
                menuInflater.inflate(R.menu.option_menu, menu)

            } else {

                val settingItem = menu?.findItem(R.id.btn_setting)
                settingItem?.icon?.setTint(ContextCompat.getColor(this, R.color.white))
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.btn_setting) {

            val intent = Intent(this, SettingActivity::class.java)
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@StoryActivity,
                    Pair(binding.edAddDescription, "name"),
                    Pair(binding.imgStoryPhoto, "logo"),
                    Pair(binding.buttonAdd, "logout")
                )
            startActivity(intent, optionsCompat.toBundle())
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}