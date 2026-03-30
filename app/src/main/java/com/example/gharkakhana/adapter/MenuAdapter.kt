package com.example.gharkakhana.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gharkakhana.DetailsActivity
import com.example.gharkakhana.databinding.MenuItemBinding
import com.example.gharkakhana.model.MenuItem

class MenuAdapter(
    private val menuItems: List<MenuItem>,       // ← fixed constructor syntax
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
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size  // ← fixed (was menuItemsName)

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)  // ← notify listener
                    openDetailActivity(position)
                }
            }
        }

        private fun openDetailActivity(position: Int) {
            val menuItem = menuItems[position]
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemImage", menuItem.foodimgurl)
                putExtra("MenuItemIngredients", menuItem.foodIngredients)
            }
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.menuFoodName.text = menuItem.foodName    // ← fixed field name
            binding.menuPrice.text = menuItem.foodPrice      // ← fixed field name
            Glide.with(requireContext)
                .load(menuItem.foodimgurl)                   // ← direct URL, no Uri.parse
                .placeholder(com.example.gharkakhana.R.drawable.ic_launcher_background)
                .error(com.example.gharkakhana.R.drawable.ic_launcher_background)
                .into(binding.menuImage)
        }
    }
}