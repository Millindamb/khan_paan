package com.example.gharkakhana.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gharkakhana.DetailsActivity
import com.example.gharkakhana.databinding.BuyAgainItemBinding

class BuyAgainAdapter(
    private val buyAgainFoodName: MutableList<String>,
    private val buyAgainFoodPrice: MutableList<String>,
    private val buyAgainFoodImage: MutableList<String>,
    private val requireContext: Context                  // ← moved to last, added Context import
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        val name  = buyAgainFoodName[position]
        val price = buyAgainFoodPrice[position]
        val image = buyAgainFoodImage[position]

        holder.bind(name, price, image)

        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName",  name)
                putExtra("MenuItemImage", image)
            }
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = buyAgainFoodName.size

    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(foodName: String, foodPrice: String, foodImage: String) { // ← String not Int
            binding.buyAgainFoodName.text  = foodName
            binding.buyAgainFoodPrice.text = foodPrice
            // ── Direct URL string — no Uri.parse needed ────────────────────
            Glide.with(requireContext)
                .load(foodImage)
                .into(binding.buyAgainFoodImage)
        }
    }
}