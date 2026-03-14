package com.example.gharkakhana

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gharkakhana.adapter.NotificationAdapter
import com.example.gharkakhana.databinding.FragmentNotifinationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Notifination_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentNotifinationBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentNotifinationBottomBinding.inflate(layoutInflater,container,false)
        val notifications=listOf("Your order has been Cancedled successfully","Order has been taken by the driver","Your order has been delivered successfully")
        val notificationImages=listOf(R.drawable.notificationimg1,R.drawable.notificationimg2,R.drawable.notificationimg3)

        val adapter= NotificationAdapter(notifications as ArrayList<String>,notificationImages as ArrayList<Int>)
//        binding.notificationRecyclerView.adapter=adapter
//        binding.notificationRecyclerView.layoutManager=LinearLayoutManager(requireContext())

        return binding.root
    }

    companion object {
    }
}