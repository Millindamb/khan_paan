package com.example.gharkakhana.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.adapter.MenuAdapter
import com.example.gharkakhana.databinding.FragmentSearchBinding
import com.example.gharkakhana.model.MenuItem
import com.example.gharkakhana.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private val originalMenuItems = mutableListOf<MenuItem>()  // ← fixed typo mutabelListOf

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        retrieveMenuItem()
        setupSearchView()

        return binding.root
    }

    private fun retrieveMenuItem() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch all menu items from Supabase ─────────────────────
                val result = SupabaseClient.client.postgrest
                    .from("menu")
                    .select()
                    .decodeList<MenuItem>()

                withContext(Dispatchers.Main) {
                    originalMenuItems.clear()
                    originalMenuItems.addAll(result)
                    showAllMenu()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load menu: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showAllMenu() {
        setAdapter(ArrayList(originalMenuItems))
    }

    private fun setAdapter(filteredMenuItem: ArrayList<MenuItem>) {
        adapter = MenuAdapter(
            menuItems = filteredMenuItem,
            requireContext = requireContext(),
            itemClickListener = object : MenuAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // handle click if needed
                }
            }
        )
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
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
        // ── If query is empty show all items ──────────────────────────────
        if (query.isBlank()) {
            showAllMenu()
            return
        }

        val filteredMenuItems = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }

        setAdapter(ArrayList(filteredMenuItems))  // ← safe cast, no force cast needed
    }
}