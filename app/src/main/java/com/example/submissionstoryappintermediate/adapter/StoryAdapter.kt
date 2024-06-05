package com.example.submissionstoryappintermediate.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionstoryappintermediate.R
import com.example.submissionstoryappintermediate.request.StoryRequest
import androidx.core.util.Pair

class StoryAdapter(private var stories: List<StoryRequest>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(story: StoryRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = stories.size

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        private val photoImageView: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private lateinit var currentStory: StoryRequest

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(storyRequest: StoryRequest) {
            currentStory = storyRequest
            nameTextView.text = storyRequest.name
            Glide.with(itemView.context).load(storyRequest.photoUrl).into(photoImageView)
        }

        override fun onClick(v: View?) {
            if (::currentStory.isInitialized) {
                listener.onItemClick(currentStory)
            }

        }
    }

    // Method to update the list of stories and notify the adapter
    fun updateStories(newStories: List<StoryRequest>) {
        stories = newStories
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }
}
