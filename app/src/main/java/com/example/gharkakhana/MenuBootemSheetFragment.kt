package com.example.gharkakhana

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.adapter.MenuAdapter
import com.example.gharkakhana.databinding.FragmentMenuBootemSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuBootemSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuBootemSheetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMenuBootemSheetBinding.inflate(inflater,container,false)

        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        val menuFoodName= listOf("Maxican Burger","Paneer Pizza","Paneer Tikka","White sauce pasta")
        val menuItemPrice= listOf("150Rs","279Rs","350Rs","150Rs")
        val menuImage= listOf(
            com.example.gharkakhana.R.drawable.burger_cart,
            com.example.gharkakhana.R.drawable.pizza_cart,
            com.example.gharkakhana.R.drawable.paneer_tikka_cart,
            com.example.gharkakhana.R.drawable.white_sauce_pasta
        )
        val adapter= MenuAdapter(ArrayList(menuFoodName), ArrayList(menuItemPrice), ArrayList(menuImage))

        binding.menuRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter=adapter

        return binding.root
    }

    companion object {
    }
}