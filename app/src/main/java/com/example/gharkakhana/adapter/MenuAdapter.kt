package com.example.gharkakhana.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gharkakhana.DetailsActivity
import com.example.gharkakhana.databinding.MenuItemBinding

class MenuAdapter(
    private val menuItemsName: MutableList<String>,
    private val menuItemPrice: MutableList<String>,
    private val menuImage: MutableList<Int>,
    private val requireContext: Context,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItemsName.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {

                    itemClickListener.onItemClick(position)

                    val intent = Intent(requireContext, DetailsActivity::class.java)
                    intent.putExtra("MenuItemName", menuItemsName[position])
                    intent.putExtra("MenuItemImage", menuImage[position])

                    requireContext.startActivity(intent)
                }
            }
        }

        fun bind(position: Int) {
            binding.menuFoodName.text = menuItemsName[position]
            binding.menuPrice.text = menuItemPrice[position]
            binding.menuImage.setImageResource(menuImage[position])
        }
    }
}