package com.example.gharkakhana

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gharkakhana.databinding.ActivityPayOutBinding
import com.example.gharkakhana.model.OrderDetails
import com.example.gharkakhana.model.UserModel
import com.example.gharkakhana.network.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class PayOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayOutBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var totalAmount: String

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

        // ── Receive data from CartFragment — only once ────────────────────
        foodItemName        = intent.getStringArrayListExtra("FoodItemName")        ?: arrayListOf()
        foodItemPrice       = intent.getStringArrayListExtra("FoodItemPrice")       ?: arrayListOf()
        foodItemImage       = intent.getStringArrayListExtra("FoodItemImage")       ?: arrayListOf()
        foodItemDescription = intent.getStringArrayListExtra("FoodItemDescription") ?: arrayListOf()
        foodItemIngredients = intent.getStringArrayListExtra("FoodItemIngredients") ?: arrayListOf()
        foodItemQuantity    = intent.getIntegerArrayListExtra("FoodItemQuantity")   ?: arrayListOf()

        // ── Calculate and show total ───────────────────────────────────────
        totalAmount = calculateTotalAmount().toString() + "$"
        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText(totalAmount)

        // ── Load saved user profile ────────────────────────────────────────
        setUserData()

        binding.PlaceMyOrder.setOnClickListener {
            val name    = binding.name.text.toString().trim()
            val address = binding.address.text.toString().trim()
            val phone   = binding.phone.text.toString().trim()

            if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ── Disable button to prevent double tap ───────────────────────
            binding.PlaceMyOrder.isEnabled = false
            binding.PlaceMyOrder.text = "Placing Order..."

            placeOrder(name, address, phone)
        }

        binding.goBackFromOrder.setOnClickListener { finish() }
    }

    private fun placeOrder(name: String, address: String, phone: String) {
        val userId = auth.currentUser?.uid ?: return

        val namesJson      = JSONArray(foodItemName).toString()
        val pricesJson     = JSONArray(foodItemPrice).toString()
        val imagesJson     = JSONArray(foodItemImage).toString()
        val quantitiesJson = JSONArray(foodItemQuantity).toString()

        val orderDetails = OrderDetails(
            userId          = userId,
            userName        = name,
            foodNames       = namesJson,
            foodPrices      = pricesJson,
            foodImages      = imagesJson,
            foodQuantities  = quantitiesJson,
            address         = address,
            phone           = phone,
            totalAmount     = totalAmount,
            orderAccepted   = false,
            paymentReceived = false
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── 1. Insert order into order_details ─────────────────────────
                SupabaseClient.client.postgrest
                    .from("order_details")
                    .insert(orderDetails)

                // ── 2. Insert same order into order_history ────────────────────
                addOrderToHistory(orderDetails)

                // ── 3. Save/update user profile ────────────────────────────────
                SupabaseClient.client.postgrest
                    .from("users")
                    .upsert(
                        UserModel(
                            id      = userId,
                            name    = name,
                            address = address,
                            phone   = phone
                        )
                    )

                // ── 4. Remove cart items for this user ─────────────────────────
                removeItemFromCart(userId)

                // ── 5. All done — show success on main thread ──────────────────
                withContext(Dispatchers.Main) {
                    binding.PlaceMyOrder.isEnabled = true
                    binding.PlaceMyOrder.text = "Place My Order"
                    Toast.makeText(
                        this@PayOutActivity,
                        "Order Placed Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val bottomSheet = CongratsBottomSheet()
                    bottomSheet.show(supportFragmentManager, "CongratsBottomSheet")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.PlaceMyOrder.isEnabled = true
                    binding.PlaceMyOrder.text = "Place My Order"
                    Toast.makeText(
                        this@PayOutActivity,
                        "Order Failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private suspend fun addOrderToHistory(orderDetails: OrderDetails) {
        SupabaseClient.client.postgrest
            .from("order_history")
            .insert(orderDetails)
    }

    private suspend fun removeItemFromCart(userId: String) {
        SupabaseClient.client.postgrest
            .from("cart")
            .delete {
                filter { eq("user_id", userId) }
            }
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in 0 until foodItemPrice.size) {
            val price = foodItemPrice[i]
            val priceIntVal = if (price.endsWith("$")) {
                price.dropLast(1).toIntOrNull() ?: 0
            } else {
                price.toIntOrNull() ?: 0
            }
            val quantity = foodItemQuantity.getOrNull(i) ?: 1
            totalAmount += priceIntVal * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid ?: return

        // ── Show Firebase Auth name immediately ────────────────────────────
        auth.currentUser?.displayName?.let {
            if (it.isNotBlank()) binding.name.setText(it)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = SupabaseClient.client.postgrest
                    .from("users")
                    .select {
                        filter { eq("id", userId) }
                    }
                    .decodeList<UserModel>()

                withContext(Dispatchers.Main) {
                    if (result.isNotEmpty()) {
                        val user = result.first()
                        if (!user.name.isNullOrBlank())    binding.name.setText(user.name)
                        if (!user.address.isNullOrBlank()) binding.address.setText(user.address)
                        if (!user.phone.isNullOrBlank())   binding.phone.setText(user.phone)
                    }
                    // ── Set hints for empty fields ─────────────────────────
                    if (binding.address.text.isNullOrBlank()) binding.address.hint = "Enter your address"
                    if (binding.phone.text.isNullOrBlank())   binding.phone.hint   = "Enter your phone number"
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
}