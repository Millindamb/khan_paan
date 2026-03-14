package com.example.gharkakhana.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gharkakhana.DetailsActivity
import com.example.gharkakhana.databinding.PopularItemBinding

class PopularAdapter(
    private val requireContext: Context,
    private val items: List<String>,
    private val prices: List<String>,
    private val images: List<Int>
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = PopularItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PopularViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val itemName = items[position]
        val itemPrice = prices[position]
        val itemImage = images[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", itemName)
            intent.putExtra("MenuItemImage", itemImage)
            requireContext.startActivity(intent)
        }

        holder.bind(itemName, itemPrice, itemImage)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PopularViewHolder(private val binding: PopularItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, price: String, image: Int) {
            binding.apply {
                foodNamePopular.text = item
                pricePopular.text = price
                imageView17.setImageResource(image)
            }
        }
    }
}