package com.example.gharkakhana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.adapter.MenuAdapter
import com.example.gharkakhana.databinding.FragmentMenuBootemSheetBinding
import com.example.gharkakhana.model.MenuItem
import com.example.gharkakhana.network.SupabaseClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuBootemSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBootemSheetBinding
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBootemSheetBinding.inflate(inflater, container, false)

        binding.buttonBack.setOnClickListener { dismiss() }

        retrieveMenuItems()

        return binding.root
    }

    private fun retrieveMenuItems() {
        menuItems = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch all rows from menu table ────────────────────────────
                val result = SupabaseClient.client.postgrest
                    .from("menu")
                    .select()
                    .decodeList<MenuItem>()

                withContext(Dispatchers.Main) {
                    menuItems.clear()
                    menuItems.addAll(result)
                    setAdapter()
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

    private fun setAdapter() {
        val adapter = MenuAdapter(
            menuItems = menuItems,
            requireContext = requireContext(),
            itemClickListener = object : MenuAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // handle item click if needed
                }
            }
        )
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): MenuBootemSheetFragment = MenuBootemSheetFragment()
    }
}