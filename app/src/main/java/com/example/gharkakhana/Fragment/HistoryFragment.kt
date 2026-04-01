package com.example.gharkakhana.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.gharkakhana.adapter.BuyAgainAdapter
import com.example.gharkakhana.databinding.FragmentHistoryBinding
import com.example.gharkakhana.model.OrderDetails
import com.example.gharkakhana.network.SupabaseClient
import com.example.gharkakhana.recentOrderItems
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private val auth = FirebaseAuth.getInstance()
    private val listOfOrderItems: MutableList<OrderDetails> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        retrieveBuyHistory()

        binding.recentBuyItem.setOnClickListener {
            seeItemsRecentBuy()
        }

        return binding.root
    }

    private fun seeItemsRecentBuy() {
        listOfOrderItems.firstOrNull()?.let { recentBuy ->
            // ── Serialize to JSON string instead of Parcelable ─────────────
            val json = kotlinx.serialization.json.Json.encodeToString(
                OrderDetails.serializer(), recentBuy
            )
            val intent = Intent(requireContext(), recentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItem", json)   // ← pass as String
            startActivity(intent)
        }
    }

    private fun retrieveBuyHistory() {
        binding.recentBuyItem.visibility = View.INVISIBLE
        val userId = auth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = SupabaseClient.client.postgrest
                    .from("order_history")
                    .select {
                        filter { eq("user_id", userId) }
                    }
                    .decodeList<OrderDetails>()

                withContext(Dispatchers.Main) {
                    listOfOrderItems.clear()
                    listOfOrderItems.addAll(result)
                    listOfOrderItems.reverse()  // most recent first

                    if (listOfOrderItems.isNotEmpty()) {
                        setDataInRecentBuyItem()
                        setPreviousBuyItemRecyclerView()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load history: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility = View.VISIBLE
        val recentOrder = listOfOrderItems.firstOrNull() ?: return

        val firstName  = parseJsonArray(recentOrder.foodNames).firstOrNull() ?: ""
        val firstPrice = parseJsonArray(recentOrder.foodPrices).firstOrNull() ?: ""
        val firstImage = parseJsonArray(recentOrder.foodImages).firstOrNull() ?: ""

        binding.againFoodName.text  = firstName
        binding.againFoodPrice.text = firstPrice
        Glide.with(requireContext()).load(firstImage).into(binding.againFoodImage)
    }

    private fun setPreviousBuyItemRecyclerView() {
        val buyAgainFoodName  = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        for (i in 1 until listOfOrderItems.size) {
            val order = listOfOrderItems[i]
            buyAgainFoodName.add(parseJsonArray(order.foodNames).firstOrNull() ?: "")
            buyAgainFoodPrice.add(parseJsonArray(order.foodPrices).firstOrNull() ?: "")
            buyAgainFoodImage.add(parseJsonArray(order.foodImages).firstOrNull() ?: "")
        }

        val adapter = BuyAgainAdapter(
            buyAgainFoodName  = buyAgainFoodName,
            buyAgainFoodPrice = buyAgainFoodPrice,
            buyAgainFoodImage = buyAgainFoodImage,
            requireContext    = requireContext()
        )
        binding.BuyAgainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.BuyAgainRecyclerView.adapter = adapter
    }

    private fun parseJsonArray(jsonString: String?): List<String> {
        if (jsonString.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(jsonString)
            List(array.length()) { i -> array.getString(i) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}