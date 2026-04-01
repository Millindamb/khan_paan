package com.example.gharkakhana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.adapter.RecentBuyAdapter
import com.example.gharkakhana.databinding.ActivityRecentOrderItemsBinding
import com.example.gharkakhana.model.OrderDetails
import org.json.JSONArray

class recentOrderItems : AppCompatActivity() {

    private val binding: ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }

    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodPrice: ArrayList<String>
    private lateinit var allFoodImage: ArrayList<String>
    private lateinit var allFoodQuantity: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        // ── Decode JSON string back to OrderDetails ────────────────────────
        val json = intent.getStringExtra("RecentBuyOrderItem")
        val orderDetails = json?.let {
            kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }.decodeFromString(OrderDetails.serializer(), it)
        }

        if (orderDetails != null) {
            allFoodNames    = parseJsonToStringList(orderDetails.foodNames)
            allFoodPrice    = parseJsonToStringList(orderDetails.foodPrices)
            allFoodImage    = parseJsonToStringList(orderDetails.foodImages)
            allFoodQuantity = parseJsonToIntList(orderDetails.foodQuantities)
        } else {
            allFoodNames    = arrayListOf()
            allFoodPrice    = arrayListOf()
            allFoodImage    = arrayListOf()
            allFoodQuantity = arrayListOf()
        }

        setAdapter()
    }

    private fun setAdapter() {
        val adapter = RecentBuyAdapter(
            context       = this,
            foodNames     = allFoodNames,
            foodPrices    = allFoodPrice,
            foodImages    = allFoodImage,
            foodQuantities = allFoodQuantity
        )
        binding.recyclerViewRecentBuy.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRecentBuy.adapter = adapter
    }

    // ── Parse JSON string → ArrayList<String> ─────────────────────────────
    private fun parseJsonToStringList(jsonString: String?): ArrayList<String> {
        val list = arrayListOf<String>()
        if (jsonString.isNullOrBlank()) return list
        return try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            list
        }
    }

    // ── Parse JSON string → ArrayList<Int> ────────────────────────────────
    private fun parseJsonToIntList(jsonString: String?): ArrayList<Int> {
        val list = arrayListOf<Int>()
        if (jsonString.isNullOrBlank()) return list
        return try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                list.add(array.getInt(i))
            }
            list
        } catch (e: Exception) {
            list
        }
    }
}