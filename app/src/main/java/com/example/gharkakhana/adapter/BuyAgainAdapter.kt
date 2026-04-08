package com.example.gharkakhana.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gharkakhana.DetailsActivity
import com.example.gharkakhana.databinding.BuyAgainItemBinding
import com.example.gharkakhana.model.CartItems
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuyAgainAdapter(
    private val buyAgainFoodName: MutableList<String>,
    private val buyAgainFoodPrice: MutableList<String>,
    private val buyAgainFoodImage: MutableList<String>,
    private val requireContext: Context
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

        // ── Open details on item click ─────────────────────────────────────
        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName",  name)
                putExtra("MenuItemImage", image)
                putExtra("MenuItemPrice", price)
            }
            requireContext.startActivity(intent)
        }

        // ── Add to cart on Buy Again button click ──────────────────────────
        holder.binding.buyAgainButton.setOnClickListener {
            addToCart(name, price, image)
        }
    }

    private fun addToCart(name: String, price: String, image: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = CartItems(
            userId       = userId,
            foodName     = name,
            foodPrice    = price,
            foodImage    = image,
            foodQuantity = 1
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.client.postgrest
                    .from("cart")
                    .insert(cartItem)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext,
                        "$name added to cart!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext,
                        "Failed to add: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = buyAgainFoodName.size

    inner class BuyAgainViewHolder(val binding: BuyAgainItemBinding) :  // ← val so button is accessible
        RecyclerView.ViewHolder(binding.root) {

        fun bind(foodName: String, foodPrice: String, foodImage: String) {
            binding.buyAgainFoodName.text  = foodName
            binding.buyAgainFoodPrice.text = foodPrice
            Glide.with(requireContext)
                .load(foodImage)
                .into(binding.buyAgainFoodImage)
        }
    }
}