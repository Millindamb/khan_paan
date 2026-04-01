package com.example.gharkakhana.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gharkakhana.databinding.RecentBuyItemBinding

class RecentBuyAdapter(
    private val context: Context,
    private val foodNames: ArrayList<String>,
    private val foodPrices: ArrayList<String>,
    private val foodImages: ArrayList<String>,
    private val foodQuantities: ArrayList<Int>
) : RecyclerView.Adapter<RecentBuyAdapter.RecentBuyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentBuyViewHolder {
        val binding = RecentBuyItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecentBuyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentBuyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNames.size

    inner class RecentBuyViewHolder(private val binding: RecentBuyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.foodName.text     = foodNames[position]
            binding.foodPrice.text    = foodPrices[position]
            binding.foodQuantity.text = "x${foodQuantities.getOrNull(position) ?: 1}"

            Glide.with(context)
                .load(foodImages.getOrNull(position) ?: "")
                .into(binding.foodImage)
        }
    }
}