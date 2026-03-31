package com.example.gharkakhana.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.PayOutActivity
import com.example.gharkakhana.adapter.CartAdapter
import com.example.gharkakhana.databinding.FragmentCartBinding
import com.example.gharkakhana.model.CartItems
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val auth = FirebaseAuth.getInstance()

    // ── Separate lists for adapter ─────────────────────────────────────────
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var foodImages: MutableList<String>   // ← String not Int
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartIds: MutableList<String>      // ← for delete

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        retrieveCartItems()

        binding.proceedButton.setOnClickListener {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun retrieveCartItems() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        foodNames        = mutableListOf()
        foodPrices       = mutableListOf()
        foodDescriptions = mutableListOf()
        foodIngredients  = mutableListOf()
        foodImages       = mutableListOf()
        quantity         = mutableListOf()
        cartIds          = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = SupabaseClient.client.postgrest
                    .from("cart")
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeList<CartItems>()

                withContext(Dispatchers.Main) {
                    for (item in result) {
                        // ── Add ALL fields together so lists stay in sync ──────
                        // ── If any field is null use empty string/default ──────
                        cartIds.add(item.id ?: "")
                        foodNames.add(item.foodName ?: "")
                        foodPrices.add(item.foodPrice ?: "")
                        foodDescriptions.add(item.foodDescription ?: "")
                        foodImages.add(item.foodImage ?: "")
                        quantity.add(item.foodQuantity ?: 1)
                        foodIngredients.add(item.foodIngredients ?: "")
                    }
                    setAdapter()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load cart: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setAdapter() {
        val adapter = CartAdapter(
            context         = requireContext(),
            cartItems       = foodNames,
            cartItemPrice   = foodPrices,
            cartImage       = foodImages,
            cartDescriptor  = foodDescriptions,
            cartQuantity    = quantity,
            cartIngredients = foodIngredients,
            cartItemIds     = cartIds            // ← pass IDs for delete
        )
        binding.cartRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.cartRecyclerView.adapter = adapter
    }
}