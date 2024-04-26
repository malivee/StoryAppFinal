package com.belajar.storyapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.belajar.storyapp.data.api.response.ListStoryItem
import com.belajar.storyapp.databinding.ItemStoriesBinding
import com.belajar.storyapp.helper.toDateFormat
import com.bumptech.glide.Glide

class HomeAdapter: RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {
    private val story = ArrayList<ListStoryItem>()
    class MyViewHolder(private val binding: ItemStoriesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.itemName.text = story.name
            binding.itemDesc.text = story.description
            binding.itemDate.text = story.createdAt?.toDateFormat()
            Glide.with(binding.root)
                .load(story.photoUrl)
                .into(binding.imgPhoto)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    fun submitList(storyList: List<ListStoryItem>) {
        story.clear()
        story.addAll(storyList)
        notifyDataSetChanged()    }

    override fun getItemCount(): Int = story.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(story[position])
    }
}