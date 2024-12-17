package com.example.foresti.home.law

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foresti.R
import com.example.foresti.databinding.ItemLawBinding

// Law.kt
data class Law(
    val id: Int,
    val name: String,
    val description: String,
    val rating: Int
)

class LawAdapter(private val onItemClick: (Law) -> Unit) :
    ListAdapter<Law, LawAdapter.LawViewHolder>(LawDiffCallback()) {

    inner class LawViewHolder(private val binding: ItemLawBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(law: Law) {
            binding.apply {
                tvLawName.text = law.name
                tvLawDescription.text = law.description

                ratingStars.removeAllViews()
                root.setOnClickListener { onItemClick(law) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LawViewHolder {
        return LawViewHolder(
            ItemLawBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LawViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class LawDiffCallback : DiffUtil.ItemCallback<Law>() {
    override fun areItemsTheSame(oldItem: Law, newItem: Law) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Law, newItem: Law) =
        oldItem == newItem
}