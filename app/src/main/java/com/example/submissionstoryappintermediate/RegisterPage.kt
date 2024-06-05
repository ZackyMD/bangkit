package com.example.submissionstoryappintermediate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.submissionstoryappintermediate.service.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RegisterPage : AppCompatActivity() {
    private val apiClient = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameEditText = findViewById<EditText>(R.id.ed_register_name)
        val emailEditText = findViewById<EditText>(R.id.ed_register_email)
        val passwordEditText = findViewById<EditText>(R.id.ed_register_password)
        val registerButton = findViewById<Button>(R.id.btn_register)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        val errorMessageTextView = findViewById<TextView>(R.id.tv_error_message)
        val loginAsGuestButton = findViewById<Button>(R.id.btn_login_as_guest)
        val loginButton = findViewById<Button>(R.id.btn_login)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                errorMessageTextView.visibility = View.VISIBLE
            } else {
                errorMessageTextView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        apiClient.register(name, email, password)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterPage, "Registration Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterPage, LoginPage::class.java))
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            if (e is HttpException && e.code() == 400) {
                                Toast.makeText(this@RegisterPage, "Email is already registered", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@RegisterPage, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } finally {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
}
