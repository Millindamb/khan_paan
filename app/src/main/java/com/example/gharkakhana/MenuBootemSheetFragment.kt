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


        val menuFoodName= listOf("Pizza","Burger","Pasta")
        val menuItemPrice= listOf("$5","$4","$6")
        val menuImage= listOf(R.drawable.ic_menu_view,R.drawable.picture_frame,R.drawable.picture_frame)
        val adapter= MenuAdapter(ArrayList(menuFoodName), ArrayList(menuItemPrice), ArrayList(menuImage))

        binding.menuRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter=adapter

        return binding.root
    }

    companion object {
    }
}