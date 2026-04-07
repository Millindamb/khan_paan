package com.example.gharkakhana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.adapter.NotificationAdapter
import com.example.gharkakhana.databinding.FragmentNotifinationBottomBinding
import com.example.gharkakhana.model.OrderDetails
import com.example.gharkakhana.network.SupabaseClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Notifination_Bottom_Fragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNotifinationBottomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifinationBottomBinding.inflate(inflater, container, false)
        fetchLastOrderAndShowNotification()
        return binding.root
    }

    private fun fetchLastOrderAndShowNotification() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Fetch ALL orders then take the last one in Kotlin ──────
                // This avoids any query-builder syntax issues entirely
                val allOrders = SupabaseClient.client.postgrest
                    .from("order_details")
                    .select()
                    .decodeList<OrderDetails>()

                withContext(Dispatchers.Main) {
                    if (allOrders.isEmpty()) {
                        showSingleNotification(
                            "No orders placed yet",
                            R.drawable.notificationimg1
                        )
                        return@withContext
                    }

                    // Sort by created_at descending and pick the first
                    val recentOrder = allOrders
                        .sortedByDescending { it.createdAt }
                        .first()

                    val (message, image) = when {
                        recentOrder.paymentReceived == true -> Pair(
                            "Your order has been delivered successfully",
                            R.drawable.notificationimg3
                        )
                        recentOrder.orderAccepted == true -> Pair(
                            "Order has been taken by the driver",
                            R.drawable.notificationimg2
                        )
                        else -> Pair(
                            "Your order is pending confirmation",
                            R.drawable.notificationimg1
                        )
                    }

                    showSingleNotification(message, image)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showSingleNotification(message: String, image: Int) {
        val adapter = NotificationAdapter(
            arrayListOf(message),
            arrayListOf(image)
        )
        binding.notificationRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter
    }

    companion object {}
}