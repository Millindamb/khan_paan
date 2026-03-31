package com.example.gharkakhana

import android.R
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.example.gharkakhana.databinding.ActivityPayOutBinding
import com.example.gharkakhana.model.UserModel
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PayOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayOutBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var totalAmount : String

    // ── Order data passed from CartFragment ───────────────────────────────
    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemDescription: ArrayList<String>
    private lateinit var foodItemIngredients: ArrayList<String>
    private lateinit var foodItemQuantity: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ── Receive data from CartFragment ────────────────────────────────
        foodItemName        = intent.getStringArrayListExtra("FoodItemName")        ?: arrayListOf()
        foodItemPrice       = intent.getStringArrayListExtra("FoodItemPrice")       ?: arrayListOf()
        foodItemImage       = intent.getStringArrayListExtra("FoodItemImage")       ?: arrayListOf()
        foodItemDescription = intent.getStringArrayListExtra("FoodItemDescription") ?: arrayListOf()
        foodItemIngredients = intent.getStringArrayListExtra("FoodItemIngredients") ?: arrayListOf()
        foodItemQuantity    = intent.getIntegerArrayListExtra("FoodItemQuantity")   ?: arrayListOf()

        // ── Load user profile from Supabase ───────────────────────────────
        setUserData()

        val intent= intent
        foodItemName= intent.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrice= intent.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
        foodItemImage= intent.getStringArrayListExtra("FoodItemImage") as ArrayList<String>
        foodItemDescription= intent.getStringArrayListExtra("FoodItemDescription") as ArrayList<String>
        foodItemIngredients= intent.getStringArrayListExtra("FoodItemIngredients") as ArrayList<String>
        foodItemQuantity= intent.getIntegerArrayListExtra("FoodItemQuantity") as ArrayList<Int>

        totalAmount=calculateTotalAmount().toString()+"$"
        binding.totalAmount.isEnabled=false
        binding.totalAmount.setText(totalAmount)

        binding.PlaceMyOrder.setOnClickListener {
            val name    = binding.name.text.toString().trim()
            val address = binding.address.text.toString().trim()
            val phone   = binding.phone.text.toString().trim()

            if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ── Save updated user info then show congrats ─────────────────
            saveUserData(name, address, phone)
        }

        binding.goBackFromOrder.setOnClickListener { finish() }
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in 0 until foodItemPrice.size) {
            val price = foodItemPrice[i]
            val lastChar = price.lastOrNull()
            val priceIntVal = if (lastChar == '$') {
                price.dropLast(1).toIntOrNull() ?: 0
            } else {
                price.toIntOrNull() ?: 0
            }
            val quantity = foodItemQuantity.getOrNull(i) ?: 0
            totalAmount += priceIntVal * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch user profile from Supabase ──────────────────────
                val result = SupabaseClient.client.postgrest
                    .from("users")
                    .select {
                        filter { eq("id", userId) }
                    }
                    .decodeList<UserModel>()

                withContext(Dispatchers.Main) {
                    if (result.isNotEmpty()) {
                        val user = result.first()
                        binding.name.setText(user.name ?: "")
                        binding.address.setText(user.address ?: "")
                        binding.phone.setText(user.phone ?: "")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PayOutActivity,
                        "Failed to load profile: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveUserData(name: String, address: String, phone: String) {
        val userId = auth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Upsert user profile ───────────────────────────────────
                SupabaseClient.client.postgrest
                    .from("users")
                    .upsert(UserModel(id = userId, name = name, address = address, phone = phone))

                withContext(Dispatchers.Main) {
                    val bottomSheet = CongratsBottomSheet()
                    bottomSheet.show(supportFragmentManager, "CongratsBottomSheet")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PayOutActivity,
                        "Failed to save details: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}