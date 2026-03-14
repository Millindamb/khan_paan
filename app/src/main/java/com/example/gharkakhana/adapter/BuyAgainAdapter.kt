package com.example.gharkakhana.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import com.example.gharkakhana.DetailsActivity
import com.example.gharkakhana.databinding.BuyAgainItemBinding

class BuyAgainAdapter(
    private val buyAgainFoodName: List<String>,
    private val buyAgainFoodPrice: List<String>,
    private val buyAgainFoodImage: List<Int>
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {

        val name = buyAgainFoodName[position]
        val price = buyAgainFoodPrice[position]
        val image = buyAgainFoodImage[position]

        holder.bind(name, price, image)

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context
            val intent = Intent(context, DetailsActivity::class.java)

            intent.putExtra("MenuItemName", name)
            intent.putExtra("MenuItemImage", image)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return buyAgainFoodName.size
    }

    class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(foodName: String, foodPrice: String, foodImage: Int) {
            binding.buyAgainFoodName.text = foodName
            binding.buyAgainFoodPrice.text = foodPrice
            binding.buyAgainFoodImage.setImageResource(foodImage)
        }
    }
}