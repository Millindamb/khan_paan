package com.example.gharkakhana

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.gharkakhana.databinding.ActivityDetailsBinding
import com.example.gharkakhana.model.CartItems
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodPrice: String? = null
    private var foodDescription: String? = null
    private var foodIngredients: String? = null

    // ── Still using Firebase Auth for user identity ────────────────────────
    private val auth = FirebaseAuth.getInstance()   // ← fixed (was lateinit wrong syntax)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ── Get data from Intent ───────────────────────────────────────────
        foodName        = intent.getStringExtra("MenuItemName")
        foodPrice       = intent.getStringExtra("MenuItemPrice")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredients = intent.getStringExtra("MenuItemIngredients")
        foodImage       = intent.getStringExtra("MenuItemImage")

        // ── Populate UI ────────────────────────────────────────────────────
        with(binding) {
            detailFoodName.text        = foodName
            detailFoodPrice.text       = foodPrice
            descriptionTextView.text   = foodDescription
            ingrediantsTextView.text   = foodIngredients
            Glide.with(this@DetailsActivity)
                .load(foodImage)           // ← direct URL, no Uri.parse needed
                .into(detailFoodImage)
        }

        binding.detailBackButton.setOnClickListener { finish() }

        binding.additemButton.setOnClickListener { addItemToCart() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addItemToCart() {
        // ── Get current logged in user ID from Firebase Auth ───────────────
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = CartItems(
            userId          = userId,
            foodName        = foodName,
            foodPrice       = foodPrice,
            foodDescription = foodDescription,
            foodImage       = foodImage,
            foodQuantity    = 1
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Insert cart item into Supabase ─────────────────────────
                SupabaseClient.client.postgrest
                    .from("cart")
                    .insert(cartItem)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetailsActivity,
                        "Item added to cart",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetailsActivity,
                        "Failed to add item: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}