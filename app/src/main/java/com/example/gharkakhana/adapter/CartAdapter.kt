package com.example.gharkakhana.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gharkakhana.databinding.CartItemBinding
import com.example.gharkakhana.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrice: MutableList<String>,
    private val cartImage: MutableList<String>,
    private val cartDescriptor: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
    private val cartIngredients: MutableList<String>,
    private val cartItemIds: MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ── itemQuantities mirrors cartQuantity passed in ──────────────────────
    private var itemQuantities = IntArray(cartItems.size) { i -> cartQuantity[i] }

    // ── Called by CartFragment to get current quantities ───────────────────
    fun getUpdatedItemQuantities(): MutableList<Int> {
        return itemQuantities.toMutableList()   // ← fixed: returns live quantities
    }

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

            Glide.with(context)
                .load(cartImage[position])
                .into(binding.cartImage)

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
            if (position < 0 || position >= cartItemIds.size) {
                Toast.makeText(context, "Invalid item position", Toast.LENGTH_SHORT).show()
                return
            }

            val itemId = cartItemIds[position]

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    SupabaseClient.client.postgrest
                        .from("cart")
                        .delete {
                            filter { eq("id", itemId) }
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