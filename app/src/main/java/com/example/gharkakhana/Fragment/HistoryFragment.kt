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
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Notification_Bottom_Fragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNotifinationBottomBinding
    private val auth = FirebaseAuth.getInstance()
    private val listOfOrderItems: MutableList<OrderDetails> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifinationBottomBinding.inflate(inflater, container, false)
        retrieveLastOrderAndShowNotification()
        return binding.root
    }

    private fun retrieveLastOrderAndShowNotification() {
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
                    listOfOrderItems.reverse() // most recent first

                    val recentOrder = listOfOrderItems.firstOrNull() ?: return@withContext

                    // ── Same logic as HistoryFragment's status color block ──
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

                    val adapter = NotificationAdapter(
                        arrayListOf(message),
                        arrayListOf(image)
                    )
                    binding.notificationRecyclerView.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.notificationRecyclerView.adapter = adapter
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load notifications: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {}
}