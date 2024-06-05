package com.example.submissionstoryappintermediate

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.submissionstoryappintermediate.service.ApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maps)

        dataStoreManager = DataStoreManager(this)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
//        // Set default location (e.g., center of the map)
//        val defaultLocation = LatLng(-6.200000, 106.816666)
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))

        // Fetch and display stories with locations
        fetchAndDisplayStories()
    }

    private fun fetchAndDisplayStories() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = getToken()
                apiClient = ApiClient(token)
                val response = withContext(Dispatchers.IO) {
                    apiClient.apiService.getStories(location = 1)
                }
                if (!response.error) {
                    response.listStory.forEach { story ->
                        val latLng = LatLng(story.lat?:0.0, story.lon?:0.0)
                        googleMap.addMarker(MarkerOptions().position(latLng).title(story.name).snippet(story.description))
                    }
                } else {
                    Log.e("MapsActivity", "Error fetching stories: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("MapsActivity", "Exception: ${e.message}", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    // Mendapatkan token dari SharedPreferences
    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, "") ?: ""
    }

    companion object {
        private const val TOKEN_KEY = "token_key"
    }
}
