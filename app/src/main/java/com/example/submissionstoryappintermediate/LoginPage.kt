package com.example.submissionstoryappintermediate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.submissionstoryappintermediate.AddStoryPage
import com.example.submissionstoryappintermediate.DataStoreManager
import com.example.submissionstoryappintermediate.R
import com.example.submissionstoryappintermediate.RegisterPage
import com.example.submissionstoryappintermediate.service.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginPage : AppCompatActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dataStoreManager = DataStoreManager(this)

        val emailEditText = findViewById<EditText>(R.id.ed_login_email)
        val passwordEditText = findViewById<EditText>(R.id.ed_login_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerButton = findViewById<Button>(R.id.btn_register)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val loginAsGuestButton = findViewById<Button>(R.id.btn_login_as_guest)

        loginAsGuestButton.setOnClickListener {
            startActivity(Intent(this@LoginPage, AddStoryPage::class.java))
        }


        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emailEditText.error = "Invalid email address"
                } else {
                    emailEditText.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val loginResponse = withContext(Dispatchers.IO) {
                            ApiClient().login(email, password)
                        }
                        if (!loginResponse.error) {
                            dataStoreManager.saveToken(loginResponse.loginResult.token, loginResponse.loginResult.name)
                            Toast.makeText(this@LoginPage, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginPage, MainActivity::class.java))
                        } else {
                            if (loginResponse.message.contains("not available", ignoreCase = true)) {
                                Toast.makeText(this@LoginPage, "Account is not available, please register first", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginPage, RegisterPage::class.java))
                            } else {
                                Toast.makeText(this@LoginPage, loginResponse.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: HttpException) {
                        Toast.makeText(this@LoginPage, "Account is not available, please register first", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginPage, RegisterPage::class.java))
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginPage, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterPage::class.java))
        }
    }
}
