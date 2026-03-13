package com.example.gharkakhana.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.adapter.MenuAdapter
import com.example.gharkakhana.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter

    private val originalMenuFoodName =
        listOf("Maxican Burger", "Paneer Pizza", "Paneer Tikka", "White sauce pasta")

    private val originalMenuItemPrice =
        listOf("150Rs", "279Rs", "350Rs", "150Rs")

    private val originalMenuImage = listOf(
        com.example.gharkakhana.R.drawable.burger_cart,
        com.example.gharkakhana.R.drawable.pizza_cart,
        com.example.gharkakhana.R.drawable.paneer_tikka_cart,
        com.example.gharkakhana.R.drawable.white_sauce_pasta
    )

    private val filteredMenuFoodName = mutableListOf<String>()
    private val filteredMenuItemPrice = mutableListOf<String>()
    private val filteredMenuImage = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Show all menu initially
        showAllMenu()

        adapter = MenuAdapter(
            filteredMenuFoodName,
            filteredMenuItemPrice,
            filteredMenuImage
        )

        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter

        setupSearchView()

        return binding.root
    }

    private fun showAllMenu() {

        filteredMenuFoodName.clear()
        filteredMenuItemPrice.clear()
        filteredMenuImage.clear()

        filteredMenuFoodName.addAll(originalMenuFoodName)
        filteredMenuItemPrice.addAll(originalMenuItemPrice)
        filteredMenuImage.addAll(originalMenuImage)
    }

    private fun setupSearchView() {

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterMenuItems(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterMenuItems(it) }
                return true
            }
        })
    }

    private fun filterMenuItems(query: String) {

        filteredMenuFoodName.clear()
        filteredMenuItemPrice.clear()
        filteredMenuImage.clear()

        originalMenuFoodName.forEachIndexed { index, foodName ->
            if (foodName.contains(query, ignoreCase = true)) {
                filteredMenuFoodName.add(foodName)
                filteredMenuItemPrice.add(originalMenuItemPrice[index])
                filteredMenuImage.add(originalMenuImage[index])
            }
        }

        adapter.notifyDataSetChanged()
    }
}