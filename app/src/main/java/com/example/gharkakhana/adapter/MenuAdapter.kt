package com.example.gharkakhana.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gharkakhana.DetailsActivity
import com.example.gharkakhana.databinding.MenuItemBinding
import com.example.gharkakhana.model.CartItems
import com.example.gharkakhana.model.MenuItem
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuItems[position]
        holder.bind(item)

        // ── Item click → open details ──────────────────────────────────────
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position)
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
        holder.binding.menuAddToCart.setOnClickListener {
            addToCart(item)
        }
    }

    private fun addToCart(item: MenuItem) {
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
            foodImage       = item.foodimgurl ?: "",
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

    inner class MenuViewHolder(val binding: MenuItemBinding) :   // ← val so button accessible
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem) {
            binding.menuFoodName.text = item.foodName ?: ""
            binding.menuPrice.text    = item.foodPrice ?: ""
            Glide.with(requireContext)
                .load(item.foodimgurl)
                .into(binding.menuImage)
        }
    }
}