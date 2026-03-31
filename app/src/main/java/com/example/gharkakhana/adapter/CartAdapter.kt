package com.example.gharkakhana.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gharkakhana.databinding.CartItemBinding
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrice: MutableList<String>,
    private val cartImage: MutableList<String>,      // ← fix: String not Int (image URLs)
    private val cartDescriptor: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
    private val cartIngredients: MutableList<String>,
    private val cartItemIds: MutableList<String>     // ← add: Supabase row IDs for delete
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private var itemQuantities = IntArray(cartItems.size) { 1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.cartFoodName.text = cartItems[position]
            binding.cartItemPrice.text = cartItemPrice[position]
            binding.cartItemQuantity.text = itemQuantities[position].toString()

            // ── Load image directly from URL ──────────────────────────────
            Glide.with(context)
                .load(cartImage[position])        // ← fixed: was loading wrong list
                .into(binding.cartImage)          // ← fixed: was passing cartImage list

            binding.minusbutton.setOnClickListener { decreaseQuantity(position) }
            binding.plusbutton.setOnClickListener { increaseQuantity(position) }
            binding.deleteButton.setOnClickListener {
                val itemPosition = adapterPosition
                if (itemPosition != RecyclerView.NO_POSITION) {
                    deleteItem(itemPosition)
                }
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            // ── Guard: make sure position is valid in cartItemIds ─────────────
            if (position < 0 || position >= cartItemIds.size) {
                Toast.makeText(context, "Invalid item position", Toast.LENGTH_SHORT).show()
                return
            }

            val itemId = cartItemIds[position]

            if (itemId.isEmpty()) {
                Toast.makeText(context, "Item ID not found", Toast.LENGTH_SHORT).show()
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    SupabaseClient.client.postgrest
                        .from("cart")
                        .delete {
                            filter {
                                eq("id", itemId)
                            }
                        }

                    withContext(Dispatchers.Main) {
                        cartItems.removeAt(position)
                        cartImage.removeAt(position)
                        cartItemPrice.removeAt(position)
                        cartDescriptor.removeAt(position)
                        cartQuantity.removeAt(position)
                        cartIngredients.removeAt(position)
                        cartItemIds.removeAt(position)
                        itemQuantities = itemQuantities
                            .filterIndexed { index, _ -> index != position }
                            .toIntArray()

                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, cartItems.size)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Failed to delete: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}