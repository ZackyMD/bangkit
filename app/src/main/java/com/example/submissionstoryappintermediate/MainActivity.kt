package com.example.submissionstoryappintermediate

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submissionstoryappintermediate.adapter.StoryPagingAdapter
import com.example.submissionstoryappintermediate.repository.StoryRepository
import com.example.submissionstoryappintermediate.request.StoryRequest
import com.example.submissionstoryappintermediate.service.ApiClient
import com.example.submissionstoryappintermediate.viewmodel.MainViewModel
import com.example.submissionstoryappintermediate.viewmodel.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), StoryPagingAdapter.OnItemClickListener {

    private lateinit var storyPagingAdapter: StoryPagingAdapter
    private lateinit var storyRepository: StoryRepository
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dataStoreManager = DataStoreManager(this)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar4)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)
        val noDataTextView = findViewById<TextView>(R.id.noDataTextView)
        val recyclerView = findViewById<RecyclerView>(R.id.list_item)
        val mapsViewButton = findViewById<FloatingActionButton>(R.id.button_maps)

        lifecycleScope.launch {
            val token = dataStoreManager.token.first()
            if (token != null) {
                saveToken(token)
            }

            val apiService = ApiClient(getToken())
            storyRepository = StoryRepository(apiService.apiService, getToken())

            mainViewModel = ViewModelProvider(this@MainActivity, ViewModelFactory(storyRepository))[MainViewModel::class.java]

            val username = dataStoreManager.username.first()
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this@MainActivity, LoginPage::class.java))
                finish()
                return@launch
            }

            Log.d("MainActivity", "Token: $token")
            val addButton = findViewById<FloatingActionButton>(R.id.button_add)
            val usernameTextView = findViewById<TextView>(R.id.username)
            usernameTextView.text = username
            val logoutButton = findViewById<Button>(R.id.btn_logout)

            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            storyPagingAdapter = StoryPagingAdapter(this@MainActivity)
            recyclerView.adapter = storyPagingAdapter

            progressBar.visibility = View.VISIBLE
            mainViewModel.getStories().observe(this@MainActivity) { pagingData ->
                progressBar.visibility = View.GONE
                if (pagingData == null) {
                    errorTextView.visibility = View.VISIBLE
                    noDataTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                    noDataTextView.visibility = View.GONE
                    storyPagingAdapter.submitData(lifecycle, pagingData)
                }
            }

            addButton.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryPage::class.java)
                startActivity(intent)
            }

            mapsViewButton.setOnClickListener {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
            }

            logoutButton.setOnClickListener {
                logout()
            }
        }

        window.sharedElementExitTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)
    }

    override fun onItemClick(story: StoryRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = dataStoreManager.token.first()
                val apiClient = ApiClient(token)

                val storyDetailResponse = withContext(Dispatchers.IO) {
                    story.id?.let { apiClient.getStoryDetail(it) }
                }

                if (storyDetailResponse != null && !storyDetailResponse.error) {
                    val intent = Intent(this@MainActivity, DetailStoryPage::class.java).apply {
                        putExtra("STORY_ID", story.id)
                        putExtra("STORY_USERNAME", story.name)
                        putExtra("STORY_PHOTO", story.photoUrl)
                        putExtra("STORY_DESCRIPTION", story.description)
                    }

                    val imageView = findViewById<ImageView>(R.id.iv_item_photo)
                    val usernameView = findViewById<TextView>(R.id.tv_item_name)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        this@MainActivity,
                        android.util.Pair(imageView, "photo"),
                        android.util.Pair(usernameView, "username")
                    )
                    startActivity(intent, options.toBundle())
                } else {
                    Log.e("ApiCall", "Error: Story detail response is null or has error")
                }
            } catch (e: Exception) {
                Log.e("ApiCall", "Error: ${e.message}", e)
            }
        }
    }

    // Menggunakan SharedPreferences untuk menyimpan token
    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    // Mendapatkan token dari SharedPreferences
    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, "") ?: ""
    }

    private fun logout() {
        lifecycleScope.launch {
            dataStoreManager.clear()
            saveToken("")
            val intent = Intent(this@MainActivity, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val TOKEN_KEY = "token_key"
    }
}
