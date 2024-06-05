package com.example.submissionstoryappintermediate

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.submissionstoryappintermediate.service.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AddStoryPage : AppCompatActivity() {

    private lateinit var previewImage: ImageView
    private var imageUri: Uri? = null
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story_page)
        dataStoreManager = DataStoreManager(this)


        previewImage = findViewById(R.id.preview_image)

        val buttonCamera = findViewById<Button>(R.id.button_camera)
        val buttonGallery = findViewById<Button>(R.id.button_gallery)
        val buttonUpload = findViewById<Button>(R.id.button_add)

        buttonCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            } else {
                openCamera()
            }
        }

        buttonGallery.setOnClickListener {
            openGallery()
        }

        buttonUpload.setOnClickListener {
            uploadStory()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.extras?.get("data") as? Bitmap
            photo?.let {
                val uri = getImageUri(it)
                previewImage.setImageBitmap(photo)
                imageUri = uri
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                previewImage.setImageURI(uri)
                imageUri = uri
            }
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun uploadStory() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar3)

        // Menampilkan progressBar saat proses upload dimulai
        progressBar.visibility = View.VISIBLE

        val descriptionText = findViewById<EditText>(R.id.ed_add_description).text.toString()
        if (imageUri == null || descriptionText.isEmpty()) {
            showToast("Please add an image and description.")
            progressBar.visibility = View.GONE
            return
        }

        // Resize the image
        val resizedBitmap = resizeBitmap(imageUri!!)

        // Convert the resized bitmap to File
        val file = bitmapToFile(resizedBitmap)

        // Prepare description and request body
        val description = descriptionText.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

        // Check if the user is a guest
        val token = getToken()

        val apiClient = ApiClient(token)

        // If the user is a guest, make a request to the guest endpoint
        if (token.isEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        apiClient.apiService.postGuestStory(
                            description,
                            body,
                            null,
                            null
                        )
                    }
                    if (!response.error) {
                        // Show success message
                        showToast("Story uploaded successfully")

                        // Redirect to MainActivity
                        reloadAddStoryPage()
                    } else {
                        // Handle error response
                        showToast("Failed to Upload Your Story")
                    }
                } catch (e: Exception) {
                    // Handle exception
                    showToast("Failed to Upload Your Story")
                } finally {
                    // Menyembunyikan progressBar saat proses upload selesai
                    progressBar.visibility = View.GONE
                }
            }
        } else {
            // If the user is logged in, make a request to the authenticated endpoint
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        apiClient.apiService.postStory(
                            description,
                            body,
                            null,
                            null
                        )
                    }

                    if (!response.error) {
                        // Show success message
                        showToast("Story uploaded successfully")

                        // Redirect to MainActivity
                        redirectToMainActivity()
                    } else {
                        // Handle error response
                        showToast("Failed to Upload Your Story")
                    }
                } catch (e: Exception) {
                    // Handle exception
                    showToast("Failed to Upload Your Story")
                } finally {
                    // Menyembunyikan progressBar saat proses upload selesai
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun reloadAddStoryPage() {
        val intent = Intent(this, AddStoryPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


    private fun resizeBitmap(imageUri: Uri): Bitmap {
        // Load the image from URI
        val originalBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

        // Define the desired width and height
        val desiredWidth = 800 // You can adjust this value as needed
        val desiredHeight = 600 // You can adjust this value as needed

        // Calculate the scale factor
        val scaleFactor = desiredWidth.toFloat() / originalBitmap.width

        // Calculate the new height maintaining aspect ratio
        val newHeight = (originalBitmap.height * scaleFactor).toInt()

        // Resize the bitmap
        return Bitmap.createScaledBitmap(originalBitmap, desiredWidth, newHeight, true)
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        // Create a file in cache directory
        val file = File(cacheDir, "resized_image.jpg")

        // Compress the bitmap and write to the file
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()

        return file
    }

    // Menggunakan SharedPreferences untuk mendapatkan token
    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, "") ?: ""
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 100
        private const val TOKEN_KEY = "token_key"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                // Permission denied
            }
        }
    }
}
