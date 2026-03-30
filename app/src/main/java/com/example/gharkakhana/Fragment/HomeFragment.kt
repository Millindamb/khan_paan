package com.example.gharkakhana.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.gharkakhana.R
import com.example.gharkakhana.MenuBootemSheetFragment
import com.example.gharkakhana.adapter.MenuAdapter
import com.example.gharkakhana.databinding.FragmentHomeBinding
import com.example.gharkakhana.model.MenuItem                  // ← correct import
import com.example.gharkakhana.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var menuItems: MutableList<MenuItem>
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBootemSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        retrieveAndDisplayPopularItems()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Image Slider ───────────────────────────────────────────────────
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.fast_food_slider, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.pizza_sliders, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.indian_thali, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {}
            override fun onItemSelected(position: Int) {
                Toast.makeText(
                    requireContext(),
                    "Selected Image $position",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun retrieveAndDisplayPopularItems() {
        menuItems = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch all menu items from Supabase ─────────────────────
                val result = SupabaseClient.client.postgrest
                    .from("menu")
                    .select()
                    .decodeList<MenuItem>()

                withContext(Dispatchers.Main) {
                    menuItems.clear()
                    menuItems.addAll(result)
                    randomPopularItems()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load items: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun randomPopularItems() {
        if (menuItems.isEmpty()) return          // ← guard against empty list

        val numItemsToShow = minOf(6, menuItems.size)   // ← avoid index overflow
        val subsetMenuItem = menuItems.shuffled().take(numItemsToShow)
        setPopularItems(subsetMenuItem)
    }

    private fun setPopularItems(subsetMenuItem: List<MenuItem>) {
        val adapter = MenuAdapter(
            menuItems = subsetMenuItem,
            requireContext = requireContext(),
            itemClickListener = object : MenuAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // handle click if needed
                }
            }
        )
        binding.popularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.popularRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}