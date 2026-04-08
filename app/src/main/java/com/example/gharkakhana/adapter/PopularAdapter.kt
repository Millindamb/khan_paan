package com.example.gharkakhana.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gharkakhana.DetailsActivity
import com.example.gharkakhana.databinding.PopularItemBinding
import com.example.gharkakhana.model.CartItems
import com.example.gharkakhana.model.MenuItem
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PopularAdapter(
    private val requireContext: Context,
    private val menuItems: List<MenuItem>       // ← use MenuItem directly, not separate lists
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = PopularItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PopularViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = menuItems[position]
        holder.bind(item)

        // ── Open details page ──────────────────────────────────────────────
        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName",        item.foodName ?: "")
                putExtra("MenuItemPrice",       item.foodPrice ?: "")
                putExtra("MenuItemDescription", item.foodDescription ?: "")
                putExtra("MenuItemImage",       item.foodimgurl ?: "")
                putExtra("MenuItemIngredients", item.foodIngredients ?: "")
            }
            requireContext.startActivity(intent)
        }

        // ── Add to cart button ─────────────────────────────────────────────
        holder.binding.addToCartPopular.setOnClickListener {
            addItemToCart(item)
        }
    }

    private fun addItemToCart(item: MenuItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = CartItems(
            userId          = userId,
            foodName        = item.foodName ?: "",
            foodPrice       = item.foodPrice ?: "",
            foodDescription = item.foodDescription ?: "",
            foodImage       = item.foodimgurl ?: "",   // ← URL string not Int
            foodIngredients = item.foodIngredients ?: "",
            foodQuantity    = 1
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.client.postgrest
                    .from("cart")
                    .insert(cartItem)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext,
                        "${item.foodName} added to cart!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext,
                        "Failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = menuItems.size

    class PopularViewHolder(val binding: PopularItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem) {
            binding.foodNamePopular.text = item.foodName ?: ""
            binding.pricePopular.text    = item.foodPrice ?: ""

            // ── Load URL image via Glide ───────────────────────────────────
            Glide.with(binding.root.context)
                .load(item.foodimgurl)
                .into(binding.imageView17)
        }
    }
}