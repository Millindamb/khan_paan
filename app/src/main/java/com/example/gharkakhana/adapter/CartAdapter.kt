package com.example.gharkakhana.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gharkakhana.databinding.CartItemBinding

class CartAdapter(
    private val cartItems: MutableList<String>,
    private val cartItemPrice: MutableList<String>,
    private val cartImage: MutableList<Int>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val itemQuantity = MutableList(cartItems.size) { 1 }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {

        val binding = CartItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            val quantity = itemQuantity[position]

            binding.cartFoodName.text = cartItems[position]
            binding.cartItemPrice.text = cartItemPrice[position]
            binding.cartImage.setImageResource(cartImage[position])
            binding.cartItemQuantity.text = quantity.toString()

            binding.minusbutton.setOnClickListener {
                decreaseQuantity(position)
            }

            binding.plusbutton.setOnClickListener {
                increaseQuantity(position)
            }

            binding.deleteButton.setOnClickListener {
                val itemPosition = adapterPosition
                if (itemPosition != RecyclerView.NO_POSITION) {
                    deleteItem(itemPosition)
                }
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantity[position] > 1) {
                itemQuantity[position]--
                binding.cartItemQuantity.text =
                    itemQuantity[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantity[position] < 10) {
                itemQuantity[position]++
                binding.cartItemQuantity.text =
                    itemQuantity[position].toString()
            }
        }

        private fun deleteItem(position: Int) {

            cartItems.removeAt(position)
            cartItemPrice.removeAt(position)
            cartImage.removeAt(position)
            itemQuantity.removeAt(position)

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
        }
    }
}