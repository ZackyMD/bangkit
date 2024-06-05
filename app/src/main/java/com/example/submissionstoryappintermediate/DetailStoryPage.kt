package com.example.submissionstoryappintermediate

import android.os.Bundle
import android.transition.TransitionInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailStoryPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_story_page)

        // Getting the data from the intent
        val storyId = intent.getStringExtra("STORY_ID")
        val storyUsername = intent.getStringExtra("STORY_USERNAME")
        val storyPhotoUrl = intent.getStringExtra("STORY_PHOTO")
        val storyDescription = intent.getStringExtra("STORY_DESCRIPTION")

        // Finding the views
        val imageView = findViewById<ImageView>(R.id.iv_detail_photo)
        val usernameView = findViewById<TextView>(R.id.tv_detail_name)
        val descriptionView = findViewById<TextView>(R.id.tv_detail_description)

        // Setting the data
        usernameView.text = storyUsername
        descriptionView.text = storyDescription
        Glide.with(this).load(storyPhotoUrl).into(imageView)

        // Setup window transitions
        window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)
    }
}
