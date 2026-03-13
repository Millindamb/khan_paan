package com.example.gharkakhana.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.R
import com.example.gharkakhana.adapter.CartAdapter
import com.example.gharkakhana.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCartBinding.inflate(inflater,container,false)

        val cartFoodName= listOf("Pizza","Burger","Pasta")
        val cartItemPrice= listOf("$5","$4","$6")
        val cartImage= listOf(R.drawable.food2_background,R.drawable.food4_background,R.drawable.food1_background)
        val adapter= CartAdapter(ArrayList(cartFoodName), ArrayList(cartItemPrice), ArrayList(cartImage))

        binding.cartRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter=adapter

        return binding.root
    }

    companion object {
    }
}