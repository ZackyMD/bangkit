package com.example.submissionstoryappintermediate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionstoryappintermediate.R
import com.example.submissionstoryappintermediate.request.StoryRequest

class StoryPagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<StoryRequest, StoryPagingAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    interface OnItemClickListener {
        fun onItemClick(story: StoryRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

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

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryRequest>() {
            override fun areItemsTheSame(oldItem: StoryRequest, newItem: StoryRequest): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryRequest, newItem: StoryRequest): Boolean {
                return oldItem == newItem
            }
        }
    }
}
