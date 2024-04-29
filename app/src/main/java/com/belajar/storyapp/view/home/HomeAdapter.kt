package com.belajar.storyapp.view.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.belajar.storyapp.view.detail.DetailActivity
import com.belajar.storyapp.data.api.response.ListStoryItem
import com.belajar.storyapp.databinding.ItemStoriesBinding
import com.belajar.storyapp.helper.toDateFormat
import com.bumptech.glide.Glide

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {
    private val story = ArrayList<ListStoryItem>()

    class MyViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.tvItemDesc.text = story.description
            binding.tvItemDate.text = story.createdAt?.toDateFormat()
            Glide.with(binding.root)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            binding.cardView.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        binding.root.context as Activity,
                        Pair(binding.ivItemPhoto, "profile"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemDesc, "description"),
                        Pair(binding.tvItemDate, "date")
                    )
                binding.root.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    fun submitList(storyList: List<ListStoryItem>) {
        story.clear()
        story.addAll(storyList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = story.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(story[position])
    }
}